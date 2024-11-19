package be.vlaanderen.informatievlaanderen.ldes.server.admin.spi;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.FragmentationConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewSpecification;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.rest.HostNamePrefixConstructor;
import org.apache.commons.io.FileUtils;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class EventStreamReaderTest {
	public static final String TIMESTAMP_PATH = "http://purl.org/dc/terms/created";
	public static final String VERSION_OF_PATH = "http://purl.org/dc/terms/isVersionOf";
	private EventStreamReader eventStreamReader;
	private Model shacl;

	@BeforeEach
	void setUp() {
		String hostName = "http://localhost:8080";
		HostNamePrefixConstructor prefixConstructor = new HostNamePrefixConstructor(hostName);
		RetentionModelExtractor retentionModelExtractor = new RetentionModelExtractor();
		ViewSpecificationConverter viewSpecificationConverter = new ViewSpecificationConverter(retentionModelExtractor,
				new FragmentationConfigExtractor(), prefixConstructor);
		KafkaSourceReader kafkaSourceReader = mock(KafkaSourceReader.class);
		eventStreamReader = new EventStreamReader(viewSpecificationConverter, retentionModelExtractor, kafkaSourceReader);
		shacl = RDFDataMgr.loadModel("shacl/collection-shape.ttl");
	}

	@Test
	void given_ModelWithViews_when_Read_then_ReturnEventStreamTo() {
		final Model eventStreamModel = RDFDataMgr.loadModel("eventstream/streams/ldes-with-named-views.ttl");
		final EventStreamTO expected = eventStreamToWithViews();

		final EventStreamTO eventStreamTO = eventStreamReader.read(eventStreamModel);

		assertThat(eventStreamTO).isEqualTo(expected);
	}

	@Test
	void given_modelWithoutViews_when_Read_then_convertToEventStreamResponse() {
		final Model eventStreamModel = RDFDataMgr.loadModel("eventstream/streams/ldes-empty.ttl");
		EventStreamTO expectedEventStreamTO = new EventStreamTO.Builder()
				.withCollection("collectionName1")
				.withTimestampPath(TIMESTAMP_PATH)
				.withVersionOfPath(VERSION_OF_PATH)
				.withShacl(shacl)
				.build();

		final EventStreamTO result = eventStreamReader.read(eventStreamModel);

		assertThat(result).isEqualTo(expectedEventStreamTO);
	}

	@Nested
	class EventStreamWithVersionCreation {
		@Test
		void given_VersionCreationEnabled_when_FromModel_then_ResultContainsDefaultDelimiter() {
			final Model eventStreamModel = RDFDataMgr.loadModel("eventstream/streams/ldes-create-versions.ttl");

			final EventStreamTO result = eventStreamReader.read(eventStreamModel);

			assertThat(result.getVersionDelimiter()).isEqualTo("/");
		}

		@Test
		void given_VersionCreationEnabledAndVersionDelimiter_when_FromModel_then_ResultContainsDefaultDelimiter() {
			final Model eventStreamModel = RDFDataMgr.loadModel("eventstream/streams/ldes-create-versions-with-custom-delimiter.nq");

			final EventStreamTO result = eventStreamReader.read(eventStreamModel);

			assertThat(result.getVersionDelimiter()).isEqualTo("&version=");
		}
	}

	@Nested
	class EventStreamWithSkoleminationDomain {
		private static final String LDES_SKOLEMIZATION_DOMAIN_KEY = "#LDES_SKOLEMIZATION_DOMAIN_LINE";
		private static final String SKOLEMIZATION_DOMAIN = "http://example.org";

		@Test
		void given_ModelWithSkolemizationDomainLiteral_when_FromModel_then_ReturnEventStreamToWithSkolemizationDomain() {
			final String modelString = readModelString()
					.replace(LDES_SKOLEMIZATION_DOMAIN_KEY, "ldes:skolemizationDomain \"" + SKOLEMIZATION_DOMAIN + "\" ;");
			final Model model = RDFParser.create().fromString(modelString).lang(Lang.TTL).toModel();

			final EventStreamTO eventStreamTO = eventStreamReader.read(model);

			assertThat(eventStreamTO.getSkolemizationDomain()).isNull();
		}

		@Test
		void given_ModelWithSkolemizationDomainResource_when_FromModel_then_ReturnEventStreamToWithSkolemizationDomain() {
			final String modelString = readModelString()
					.replace(LDES_SKOLEMIZATION_DOMAIN_KEY, "ldes:skolemizationDomain <" + SKOLEMIZATION_DOMAIN + "> ;");
			final Model model = RDFParser.create().fromString(modelString).lang(Lang.TTL).toModel();

			final EventStreamTO eventStreamTO = eventStreamReader.read(model);

			assertThat(eventStreamTO.getSkolemizationDomain()).isEqualTo(SKOLEMIZATION_DOMAIN);
		}

		@Test
		void given_ModelWithoutSkolemization_when_FromModel_then_ReturnEventStreamToWitouthSkolemizationDomain() {
			final String modelString = readModelString().replace(LDES_SKOLEMIZATION_DOMAIN_KEY, "");
			final Model model = RDFParser.create().fromString(modelString).lang(Lang.TTL).toModel();

			final EventStreamTO eventStreamTO = eventStreamReader.read(model);

			assertThat(eventStreamTO.getSkolemizationDomain()).isNull();
		}

		private String readModelString() {
			try {
				final File file = ResourceUtils.getFile("classpath:eventstream/streams/ldes-with-skol-dom.ttl");
				return FileUtils.readFileToString(file, StandardCharsets.UTF_8);
			} catch (IOException e) {
				throw new UncheckedIOException(e);
			}
		}
	}

	private EventStreamTO eventStreamToWithViews() {
		FragmentationConfig fragmentationConfig = new FragmentationConfig();
		fragmentationConfig.setName("ExampleFragmentation");
		fragmentationConfig.setConfig(Map.of("property", "ldes:propertyPath"));
		return new EventStreamTO.Builder()
				.withCollection("collectionName1")
				.withTimestampPath(TIMESTAMP_PATH)
				.withVersionOfPath(VERSION_OF_PATH)
				.withShacl(shacl)
				.withViews(List.of(
						new ViewSpecification(
								new ViewName("collectionName1", "view2"),
								List.of(),
								List.of(fragmentationConfig), 100),
						new ViewSpecification(
								new ViewName("collectionName1", "view1"),
								List.of(),
								List.of(fragmentationConfig), 100)))
				.build();
	}

}