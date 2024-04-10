package be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.eventstream.services;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.dcat.dcatdataset.entities.DcatDataset;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.spi.*;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.PrefixAdder;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.PrefixAdderImpl;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.FragmentationConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewSpecification;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.rest.PrefixConstructor;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.riot.RDFDataMgr;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.apache.jena.rdf.model.ResourceFactory.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class EventStreamConverterImplTest {
	private EventStreamConverter eventStreamConverter;
	private Model shacl;
	private Model dataSetModel;

	@BeforeEach
	void setUp() throws URISyntaxException {
		String hostName = "http://localhost:8080";
		PrefixConstructor prefixConstructor = new PrefixConstructor(hostName, false);
		ViewSpecificationConverter viewSpecificationConverter = new ViewSpecificationConverter(new RetentionModelExtractor(),
				new FragmentationConfigExtractor(), prefixConstructor);
		PrefixAdder prefixAdder = new PrefixAdderImpl();
		eventStreamConverter = new EventStreamConverterImpl(viewSpecificationConverter, prefixAdder, prefixConstructor);
		shacl = readModelFromFile("eventstream/streams/server-shape.ttl");
		dataSetModel = readModelFromFile("dcat/dataset/valid.ttl");
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
			EventStreamTO expectedEventStreamTO = new EventStreamTO("collectionName1",
					"http://purl.org/dc/terms/created", "http://purl.org/dc/terms/isVersionOf", false,
					views, shacl);

			EventStreamTO result = eventStreamConverter.fromModel(eventStreamModel);

			assertEquals(expectedEventStreamTO, result);
		}

		@Test
		void when_modelCreateVersions_then_convertToEventStreamResponse() {
			EventStreamTO expectedEventStreamTO = new EventStreamTO("collectionName1",
					"http://purl.org/dc/terms/created", "http://purl.org/dc/terms/isVersionOf", true,
					views, shacl);
			eventStreamModel.remove(eventStreamModel.listStatements(null,
					createProperty("https://w3id.org/ldes#createVersions"), (RDFNode) null));
			eventStreamModel.add(createResource("http://localhost:8080/collectionName1"),
					createProperty("https://w3id.org/ldes#createVersions"), createTypedLiteral(true));

			EventStreamTO result = eventStreamConverter.fromModel(eventStreamModel);

			assertEquals(expectedEventStreamTO, result);
		}

		@Test
		void when_eventStreamHasViews_then_convertToModel() {
			final EventStreamTO eventStream = new EventStreamTO("collectionName1",
					"http://purl.org/dc/terms/created", "http://purl.org/dc/terms/isVersionOf", false,
					views, shacl);

			final Model convertedModel = eventStreamConverter.toModel(eventStream);

			assertThat(convertedModel).matches(eventStreamModel::isIsomorphicWith);
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
			EventStreamTO expectedEventStreamTO = new EventStreamTO("collectionName1",
					"http://purl.org/dc/terms/created", "http://purl.org/dc/terms/isVersionOf", false,
					List.of(),
					shacl);

			assertEquals(expectedEventStreamTO, eventStreamConverter.fromModel(eventStreamModel));
		}

		@Test
		void when_eventStreamResponseHasNoViews_then_convertToModel() {
			final EventStreamTO eventStream = new EventStreamTO("collectionName1",
					"http://purl.org/dc/terms/created", "http://purl.org/dc/terms/isVersionOf", false,
					List.of(), shacl);
			final Model convertedModel = eventStreamConverter.toModel(eventStream);
			assertThat(convertedModel).matches(eventStreamModel::isIsomorphicWith);
		}

		@Test
		void when_eventStreamCreateVersions_then_convertToModel() {
			final EventStreamTO eventStream = new EventStreamTO("collectionName1",
					"http://purl.org/dc/terms/created", "http://purl.org/dc/terms/isVersionOf", true,
					List.of(), shacl);
			final Model convertedModel = eventStreamConverter.toModel(eventStream);

			eventStreamModel.remove(eventStreamModel.listStatements(null,
					createProperty("https://w3id.org/ldes#createVersions"), (RDFNode) null));
			eventStreamModel.add(createResource("http://localhost:8080/collectionName1"),
					createProperty("https://w3id.org/ldes#createVersions"), createTypedLiteral(true));

			assertThat(convertedModel).matches(eventStreamModel::isIsomorphicWith);
		}

		@Test
		void when_eventStreamResponseHasTimestampAndVersionOf_then_convertToModel() {
			EventStreamTO eventStream = new EventStreamTO("collectionName1",
					null, null, false,
					List.of(),
					shacl);

			eventStreamModel.remove(eventStreamModel.listStatements(null,
					createProperty("https://w3id.org/ldes#versionOfPath"), (RDFNode) null));
			eventStreamModel.remove(eventStreamModel.listStatements(null,
					createProperty("https://w3id.org/ldes#timestampPath"), (RDFNode) null));

			final Model convertedModel = eventStreamConverter.toModel(eventStream);

			assertThat(convertedModel).matches(eventStreamModel::isIsomorphicWith);
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
			final EventStreamTO eventStream = new EventStreamTO("collectionName1",
					"http://purl.org/dc/terms/created", "http://purl.org/dc/terms/isVersionOf",
                    true, views, shacl,
					new DcatDataset("collectionName1", dataSetModel));
			final Model convertedModel = eventStreamConverter.toModel(eventStream);

			assertThat(convertedModel).matches(eventStreamModel::isIsomorphicWith);
		}
	}

	private Model readModelFromFile(String fileName) throws URISyntaxException {
		ClassLoader classLoader = getClass().getClassLoader();
		String uri = Objects.requireNonNull(classLoader.getResource(fileName)).toURI().toString();
		return RDFDataMgr.loadModel(uri);
	}

}
