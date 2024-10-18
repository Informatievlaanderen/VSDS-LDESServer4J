package be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.eventstream.services;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.dcat.dcatdataset.entities.DcatDataset;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.spi.*;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.PrefixAdder;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.PrefixAdderImpl;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.FragmentationConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewSpecification;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.rest.PrefixConstructor;
import org.apache.commons.io.FileUtils;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFParser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class EventStreamConverterImplTest {
	public static final String TIMESTAMP_PATH = "http://purl.org/dc/terms/created";
	public static final String VERSION_OF_PATH = "http://purl.org/dc/terms/isVersionOf";
	private EventStreamConverter eventStreamConverter;
	private Model shacl;
	private Model dataSetModel;
	private List<Model> eventSourceRetentionModels;

	@BeforeEach
	void setUp() throws URISyntaxException {
		String hostName = "http://localhost:8080";
		PrefixConstructor prefixConstructor = new PrefixConstructor(hostName, false);
		RetentionModelExtractor retentionModelExtractor = new RetentionModelExtractor();
		ViewSpecificationConverter viewSpecificationConverter = new ViewSpecificationConverter(retentionModelExtractor,
				new FragmentationConfigExtractor(), prefixConstructor);
		PrefixAdder prefixAdder = new PrefixAdderImpl();
		eventStreamConverter = new EventStreamConverterImpl(viewSpecificationConverter, retentionModelExtractor, prefixAdder, prefixConstructor);
		shacl = readModelFromFile("shacl/collection-shape.ttl");
		dataSetModel = readModelFromFile("dcat/dataset/valid.ttl");
		Model retentionModel = readModelFromFile("retention/example_timebased.ttl");
		eventSourceRetentionModels = List.of(retentionModel);
	}

	@Nested
	class EventStreamWithViews {
		private List<ViewSpecification> views;
		private Model eventStreamModel;


		@BeforeEach
		void setUp() throws URISyntaxException {
			FragmentationConfig fragmentationConfig = new FragmentationConfig();
			fragmentationConfig.setName("ExampleFragmentation");
			fragmentationConfig.setConfig(Map.of("property", "ldes:propertyPath"));
			views = List.of(
					new ViewSpecification(
							new ViewName("collectionName1", "view2"),
							List.of(),
							List.of(fragmentationConfig), 100),
					new ViewSpecification(
							new ViewName("collectionName1", "view1"),
							List.of(),
							List.of(fragmentationConfig), 100));
			eventStreamModel = readModelFromFile("eventstream/streams/ldes-with-named-views.ttl");
		}

		@Test
		void when_modelHasViews_then_convertToEventStreamResponse() {
			EventStreamTO expectedEventStreamTO = getBaseBuilder()
					.withViews(views)
					.withShacl(shacl)
					.build();

			EventStreamTO result = eventStreamConverter.fromModel(eventStreamModel);

			Assertions.assertEquals(expectedEventStreamTO, result);
		}

		@Test
		void when_modelCreateVersions_then_convertToEventStreamResponse() {
			EventStreamTO expectedEventStreamTO = getBaseBuilder()
					.withViews(views)
					.withShacl(shacl)
					.withVersionCreationEnabled(true)
					.build();
			eventStreamModel.remove(eventStreamModel.listStatements(null,
					ResourceFactory.createProperty("https://w3id.org/ldes#createVersions"), (RDFNode) null));
			eventStreamModel.add(ResourceFactory.createResource("http://localhost:8080/collectionName1"),
					ResourceFactory.createProperty("https://w3id.org/ldes#createVersions"), ResourceFactory.createTypedLiteral(true));

			EventStreamTO result = eventStreamConverter.fromModel(eventStreamModel);

			Assertions.assertEquals(expectedEventStreamTO, result);
		}

		@Test
		void when_eventStreamHasViews_then_convertToModel() {
			final EventStreamTO eventStream = getBaseBuilder()
					.withViews(views)
					.withShacl(shacl)
					.withEventSourceRetentionPolicies(eventSourceRetentionModels)
					.build();

			final Model convertedModel = eventStreamConverter.toModel(eventStream);

			assertThat(convertedModel).matches(eventStreamModel::isIsomorphicWith);
		}

		private static EventStreamTO.Builder getBaseBuilder() {
			return new EventStreamTO.Builder()
					.withCollection("collectionName1")
					.withTimestampPath(TIMESTAMP_PATH)
					.withVersionOfPath(VERSION_OF_PATH);
		}
	}

	@Nested
	class EventStreamWithoutViews {
		private Model eventStreamModel;

		@BeforeEach
		void setUp() throws URISyntaxException {
			eventStreamModel = readModelFromFile("eventstream/streams/ldes-empty.ttl");
		}

		@Test
		void when_modelHasNoViews_then_convertToEventStreamResponse() {
			EventStreamTO expectedEventStreamTO = getBaseBuilder().withShacl(shacl).build();

			assertEquals(expectedEventStreamTO, eventStreamConverter.fromModel(eventStreamModel));
		}

		@Test
		void when_eventStreamResponseHasNoViews_then_convertToModel() {
			final EventStreamTO eventStream = getBaseBuilder()
					.withShacl(shacl)
					.withEventSourceRetentionPolicies(eventSourceRetentionModels)
					.build();
			final Model convertedModel = eventStreamConverter.toModel(eventStream);
			assertThat(convertedModel).matches(eventStreamModel::isIsomorphicWith);
		}

		@Test
		void when_eventStreamCreateVersions_then_convertToModel() {
			final EventStreamTO eventStream = getBaseBuilder()
					.withShacl(shacl)
					.withVersionCreationEnabled(true)
					.withEventSourceRetentionPolicies(eventSourceRetentionModels)
					.build();
			final Model convertedModel = eventStreamConverter.toModel(eventStream);

			eventStreamModel.remove(eventStreamModel.listStatements(null,
					ResourceFactory.createProperty("https://w3id.org/ldes#createVersions"), (RDFNode) null));
			eventStreamModel.add(ResourceFactory.createResource("http://localhost:8080/collectionName1"),
					ResourceFactory.createProperty("https://w3id.org/ldes#createVersions"), ResourceFactory.createTypedLiteral(true));

			assertThat(convertedModel).matches(eventStreamModel::isIsomorphicWith);
		}

		@Test
		void when_eventStreamResponseHasTimestampAndVersionOf_then_convertToModel() {
			EventStreamTO eventStream = getBaseBuilder()
					.withShacl(shacl)
					.withTimestampPath(null)
					.withVersionOfPath(null)
					.withEventSourceRetentionPolicies(eventSourceRetentionModels)
					.build();

			eventStreamModel.remove(eventStreamModel.listStatements(null,
					ResourceFactory.createProperty("https://w3id.org/ldes#versionOfPath"), (RDFNode) null));
			eventStreamModel.remove(eventStreamModel.listStatements(null,
					ResourceFactory.createProperty("https://w3id.org/ldes#timestampPath"), (RDFNode) null));

			final Model convertedModel = eventStreamConverter.toModel(eventStream);

			assertThat(convertedModel).matches(eventStreamModel::isIsomorphicWith);
		}

		private EventStreamTO.Builder getBaseBuilder() {
			return new EventStreamTO.Builder()
					.withCollection("collectionName1")
					.withTimestampPath(TIMESTAMP_PATH)
					.withVersionOfPath(VERSION_OF_PATH);
		}
	}

	@Nested
	class EventStreamWithViewsAndDataset {
		private List<ViewSpecification> views;
		private Model eventStreamModel;

		@BeforeEach
		void setUp() throws URISyntaxException {
			FragmentationConfig fragmentationConfig = new FragmentationConfig();
			fragmentationConfig.setName("ExampleFragmentation");
			fragmentationConfig.setConfig(Map.of("property", "ldes:propertyPath"));
			views = List.of(
					new ViewSpecification(
							new ViewName("collectionName1", "view2"),
							List.of(),
							List.of(fragmentationConfig), 100),
					new ViewSpecification(
							new ViewName("collectionName1", "view1"),
							List.of(),
							List.of(fragmentationConfig), 100));

			eventStreamModel = readModelFromFile("eventstream/streams/ldes-and-dataset-with-named-views.ttl");
		}

		@Test
		void when_eventStreamHasViewsAndDataset_Then_ConvertToModel() {
			String collectionName = "collectionName1";
			final EventStreamTO eventStream = new EventStreamTO.Builder()
					.withCollection(collectionName)
					.withTimestampPath(TIMESTAMP_PATH)
					.withVersionOfPath(VERSION_OF_PATH)
					.withVersionCreationEnabled(true)
					.withViews(views)
					.withShacl(shacl)
					.withEventSourceRetentionPolicies(eventSourceRetentionModels)
					.withDcatDataset(new DcatDataset(collectionName, dataSetModel))
					.build();
			final Model convertedModel = eventStreamConverter.toModel(eventStream);

			assertThat(convertedModel).matches(eventStreamModel::isIsomorphicWith);
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
			final Model model = RDFParser.fromString(modelString).lang(Lang.TTL).toModel();

			final EventStreamTO eventStreamTO = eventStreamConverter.fromModel(model);

			assertThat(eventStreamTO.getSkolemizationDomain()).isNull();
		}

		@Test
		void given_ModelWithSkolemizationDomainResource_when_FromModel_then_ReturnEventStreamToWithSkolemizationDomain() {
			final String modelString = readModelString()
					.replace(LDES_SKOLEMIZATION_DOMAIN_KEY, "ldes:skolemizationDomain <" + SKOLEMIZATION_DOMAIN + "> ;");
			final Model model = RDFParser.fromString(modelString).lang(Lang.TTL).toModel();

			final EventStreamTO eventStreamTO = eventStreamConverter.fromModel(model);

			assertThat(eventStreamTO.getSkolemizationDomain()).isEqualTo(SKOLEMIZATION_DOMAIN);
		}

		@Test
		void given_ModelWithoutSkolemization_when_FromModel_then_ReturnEventStreamToWitouthSkolemizationDomain() {
			final String modelString = readModelString().replace(LDES_SKOLEMIZATION_DOMAIN_KEY, "");
			final Model model = RDFParser.fromString(modelString).lang(Lang.TTL).toModel();

			final EventStreamTO eventStreamTO = eventStreamConverter.fromModel(model);

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

	private Model readModelFromFile(String fileName) throws URISyntaxException {
		ClassLoader classLoader = getClass().getClassLoader();
		String uri = Objects.requireNonNull(classLoader.getResource(fileName)).toURI().toString();
		return RDFDataMgr.loadModel(uri);
	}

}
