package be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.PrefixAdder;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.PrefixAdderImpl;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.dcatdataset.entities.DcatDataset;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.http.services.EventStreamResponseConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.http.services.EventStreamResponseConverterImpl;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.http.valueobjects.EventStreamResponse;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.view.service.FragmentationConfigExtractor;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.view.service.RetentionModelExtractor;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.view.service.ViewSpecificationConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.AppConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.FragmentationConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewSpecification;
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

import static org.apache.jena.rdf.model.ResourceFactory.createProperty;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EventStreamResponseConverterImplTest {
	private EventStreamResponseConverter eventStreamConverter;
	private Model shacl;
	private Model dataSetModel;

	@BeforeEach
	void setUp() throws URISyntaxException {
		AppConfig appConfig = new AppConfig();
		appConfig.setHostName("http://localhost:8080");
		ViewSpecificationConverter viewSpecificationConverter = new ViewSpecificationConverter(appConfig,
				new RetentionModelExtractor(), new FragmentationConfigExtractor());
		PrefixAdder prefixAdder = new PrefixAdderImpl();
		eventStreamConverter = new EventStreamResponseConverterImpl(appConfig, viewSpecificationConverter, prefixAdder);
		shacl = readModelFromFile("eventstream/streams/example-shape.ttl");
		dataSetModel = readModelFromFile("dcat-dataset/valid.ttl");
	}

	@Nested
	class EventStreamWithViews {
		private List<ViewSpecification> views;
		private Model eventStreamModel;

		@BeforeEach
		void setUp() throws URISyntaxException {
			FragmentationConfig fragmentationConfig = new FragmentationConfig();
			fragmentationConfig.setName("fragmentationStrategy");
			fragmentationConfig.setConfig(Map.of("property", "ldes:propertyPath"));
			views = List.of(
					new ViewSpecification(
							new ViewName("collectionName1", "view2"),
							List.of(),
							List.of(fragmentationConfig)),
					new ViewSpecification(
							new ViewName("collectionName1", "view1"),
							List.of(),
							List.of(fragmentationConfig)));

			eventStreamModel = readModelFromFile("eventstream/streams/ldes-with-named-views.ttl");
		}

		@Test
		void when_modelHasViews_then_convertToEventStreamResponse() {
			EventStreamResponse expectedEventStreamResponse = new EventStreamResponse("collectionName1",
					"http://purl.org/dc/terms/created", "http://purl.org/dc/terms/isVersionOf",
					"https://data.vlaanderen.be/ns/mobiliteit#Mobiliteitshinder", false, views, shacl);

			EventStreamResponse result = eventStreamConverter.fromModel(eventStreamModel);

			assertEquals(expectedEventStreamResponse, result);
		}

		@Test
		void when_eventStreamHasViews_then_convertToModel() {
			final EventStreamResponse eventStream = new EventStreamResponse("collectionName1",
					"http://purl.org/dc/terms/created", "http://purl.org/dc/terms/isVersionOf",
					"https://data.vlaanderen.be/ns/mobiliteit#Mobiliteitshinder",
					false, views, shacl);
			final Model convertedModel = eventStreamConverter.toModel(eventStream);
			assertTrue(eventStreamModel.isIsomorphicWith(convertedModel));
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
			EventStreamResponse expectedEventStreamResponse = new EventStreamResponse("collectionName1",
					"http://purl.org/dc/terms/created", "http://purl.org/dc/terms/isVersionOf",
					"https://data.vlaanderen.be/ns/mobiliteit#Mobiliteitshinder", true, List.of(),
					shacl);

			assertEquals(expectedEventStreamResponse, eventStreamConverter.fromModel(eventStreamModel));
		}

		@Test
		void when_eventStreamResponseHasNoViews_then_convertToModel() {
			final EventStreamResponse eventStream = new EventStreamResponse("collectionName1",
					"http://purl.org/dc/terms/created", "http://purl.org/dc/terms/isVersionOf",
					"https://data.vlaanderen.be/ns/mobiliteit#Mobiliteitshinder",
					true, List.of(), shacl);
			final Model convertedModel = eventStreamConverter.toModel(eventStream);
			assertTrue(eventStreamModel.isIsomorphicWith(convertedModel));
		}

		@Test
		void when_eventStreamResponseHasTimestampAndVersionOf_then_convertToModel() {
			EventStreamResponse eventStream = new EventStreamResponse("collectionName1",
					null, null,
					"https://data.vlaanderen.be/ns/mobiliteit#Mobiliteitshinder", true, List.of(),
					shacl);

			eventStreamModel.remove(eventStreamModel.listStatements(null,
					createProperty("https://w3id.org/ldes#versionOfPath"), (RDFNode) null));
			eventStreamModel.remove(eventStreamModel.listStatements(null,
					createProperty("https://w3id.org/ldes#timestampPath"), (RDFNode) null));

			final Model convertedModel = eventStreamConverter.toModel(eventStream);

			assertTrue(eventStreamModel.isIsomorphicWith(convertedModel));
		}
	}

	@Nested
	class EventStreamWithViewsAndDataset {
		private List<ViewSpecification> views;
		private Model eventStreamModel;

		@BeforeEach
		void setUp() throws URISyntaxException {
			FragmentationConfig fragmentationConfig = new FragmentationConfig();
			fragmentationConfig.setName("fragmentationStrategy");
			fragmentationConfig.setConfig(Map.of("property", "ldes:propertyPath"));
			views = List.of(
					new ViewSpecification(
							new ViewName("collectionName1", "view2"),
							List.of(),
							List.of(fragmentationConfig)),
					new ViewSpecification(
							new ViewName("collectionName1", "view1"),
							List.of(),
							List.of(fragmentationConfig)));

			eventStreamModel = readModelFromFile("eventstream/streams/ldes-and-dataset-with-named-views.ttl");
		}

		@Test
		void when_eventStreamHasViewsAndDataset_Then_ConvertToModel() {
			final EventStreamResponse eventStream = new EventStreamResponse("collectionName1",
					"http://purl.org/dc/terms/created", "http://purl.org/dc/terms/isVersionOf",
					"https://data.vlaanderen.be/ns/mobiliteit#Mobiliteitshinder",
					false, views, shacl, new DcatDataset("collectionName1", dataSetModel));
			final Model convertedModel = eventStreamConverter.toModel(eventStream);

			assertTrue(eventStreamModel.isIsomorphicWith(convertedModel));
		}
	}

	private Model readModelFromFile(String fileName) throws URISyntaxException {
		ClassLoader classLoader = getClass().getClassLoader();
		String uri = Objects.requireNonNull(classLoader.getResource(fileName)).toURI().toString();
		return RDFDataMgr.loadModel(uri);
	}

}
