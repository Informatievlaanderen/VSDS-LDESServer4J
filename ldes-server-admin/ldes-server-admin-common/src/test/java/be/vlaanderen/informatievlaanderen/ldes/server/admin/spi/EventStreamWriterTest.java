package be.vlaanderen.informatievlaanderen.ldes.server.admin.spi;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.dcat.dcatdataset.entities.DcatDataset;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.PrefixAdderImpl;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.FragmentationConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewSpecification;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.rest.PrefixConstructor;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.riot.RDFDataMgr;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class EventStreamWriterTest {
	private static final String TIMESTAMP_PATH = "http://purl.org/dc/terms/created";
	private static final String VERSION_OF_PATH = "http://purl.org/dc/terms/isVersionOf";
	private static final String COLLECTION_NAME = "collectionName1";
	private EventStreamWriter eventStreamWriter;
	private Model shacl;
	private Model dataSetModel;
	private List<Model> eventSourceRetentionModels;

	@BeforeEach
	void setUp() {
		String hostName = "http://localhost:8080";
		PrefixConstructor prefixConstructor = new PrefixConstructor(hostName, false);
		ViewSpecificationConverter viewSpecificationConverter = new ViewSpecificationConverter(
				new RetentionModelExtractor(),
				new FragmentationConfigExtractor(),
				prefixConstructor
		);
		eventStreamWriter = new EventStreamWriter(viewSpecificationConverter, new PrefixAdderImpl(), prefixConstructor);
		shacl = RDFDataMgr.loadModel("shacl/collection-shape.ttl");
		dataSetModel = RDFDataMgr.loadModel("dcat/dataset/valid.ttl");
		eventSourceRetentionModels = List.of(RDFDataMgr.loadModel("retention/example_timebased.ttl"));
	}

	@Nested
	class EventStreamWithViews {
		private List<ViewSpecification> views;
		private Model eventStreamModel;

		@BeforeEach
		void setUp() {
			views = viewSpecifications();
			eventStreamModel = RDFDataMgr.loadModel("eventstream/streams/ldes-with-named-views.ttl");
		}

		@Test
		void when_eventStreamHasViews_then_convertToModel() {
			final EventStreamTO eventStream = baseBuilder()
					.withViews(views)
					.withShacl(shacl)
					.withEventSourceRetentionPolicies(eventSourceRetentionModels)
					.build();

			final Model convertedModel = eventStreamWriter.write(eventStream);

			assertThat(convertedModel).matches(eventStreamModel::isIsomorphicWith);
		}
	}

	@Nested
	class EventStreamWithoutViews {
		private Model eventStreamModel;

		@BeforeEach
		void setUp() {
			eventStreamModel = RDFDataMgr.loadModel("eventstream/streams/ldes-empty.ttl");
		}

		@Test
		void when_eventStreamResponseHasNoViews_then_convertToModel() {
			final EventStreamTO eventStream = baseBuilder()
					.withShacl(shacl)
					.withEventSourceRetentionPolicies(eventSourceRetentionModels)
					.build();
			final Model convertedModel = eventStreamWriter.write(eventStream);
			assertThat(convertedModel).matches(eventStreamModel::isIsomorphicWith);
		}

		@Test
		void when_eventStreamCreateVersions_then_convertToModel() {
			final EventStreamTO eventStream = baseBuilder()
					.withShacl(shacl)
					.withVersionDelimiter("/")
					.withEventSourceRetentionPolicies(eventSourceRetentionModels)
					.build();
			final Model convertedModel = eventStreamWriter.write(eventStream);

			eventStreamModel.remove(eventStreamModel.listStatements(null,
					ResourceFactory.createProperty("https://w3id.org/ldes#createVersions"), (RDFNode) null));
			eventStreamModel.add(ResourceFactory.createResource("http://localhost:8080/collectionName1"),
					ResourceFactory.createProperty("https://w3id.org/ldes#createVersions"), ResourceFactory.createTypedLiteral(true));

			assertThat(convertedModel).matches(eventStreamModel::isIsomorphicWith);
		}

		@Test
		void when_eventStreamResponseHasTimestampAndVersionOf_then_convertToModel() {
			EventStreamTO eventStream = new EventStreamTO.Builder()
					.withCollection(COLLECTION_NAME)
					.withShacl(shacl)
					.withEventSourceRetentionPolicies(eventSourceRetentionModels)
					.build();

			eventStreamModel.remove(eventStreamModel.listStatements(null,
					ResourceFactory.createProperty("https://w3id.org/ldes#versionOfPath"), (RDFNode) null));
			eventStreamModel.remove(eventStreamModel.listStatements(null,
					ResourceFactory.createProperty("https://w3id.org/ldes#timestampPath"), (RDFNode) null));

			final Model convertedModel = eventStreamWriter.write(eventStream);

			assertThat(convertedModel).matches(eventStreamModel::isIsomorphicWith);
		}
	}

	@Nested
	class EventStreamWithViewsAndDataset {
		private List<ViewSpecification> views;
		private Model eventStreamModel;

		@BeforeEach
		void setUp() {
			views = viewSpecifications();
			eventStreamModel = RDFDataMgr.loadModel("eventstream/streams/ldes-and-dataset-with-named-views.ttl");
		}

		@Test
		void when_eventStreamHasViewsAndDataset_Then_ConvertToModel() {
			final EventStreamTO eventStream = baseBuilder()
					.withVersionDelimiter("/")
					.withViews(views)
					.withShacl(shacl)
					.withEventSourceRetentionPolicies(eventSourceRetentionModels)
					.withDcatDataset(new DcatDataset(COLLECTION_NAME, dataSetModel))
					.build();
			final Model convertedModel = eventStreamWriter.write(eventStream);

			assertThat(convertedModel).matches(eventStreamModel::isIsomorphicWith);
		}
	}

	private static EventStreamTO.Builder baseBuilder() {
		return new EventStreamTO.Builder()
				.withCollection(COLLECTION_NAME)
				.withTimestampPath(TIMESTAMP_PATH)
				.withVersionOfPath(VERSION_OF_PATH);
	}

	private List<ViewSpecification> viewSpecifications() {
		FragmentationConfig fragmentationConfig = new FragmentationConfig();
		fragmentationConfig.setName("ExampleFragmentation");
		fragmentationConfig.setConfig(Map.of("property", "ldes:propertyPath"));
		return List.of(
				new ViewSpecification(
						new ViewName(COLLECTION_NAME, "view2"),
						List.of(),
						List.of(fragmentationConfig), 100),
				new ViewSpecification(
						new ViewName(COLLECTION_NAME, "view1"),
						List.of(),
						List.of(fragmentationConfig), 100));
	}
}