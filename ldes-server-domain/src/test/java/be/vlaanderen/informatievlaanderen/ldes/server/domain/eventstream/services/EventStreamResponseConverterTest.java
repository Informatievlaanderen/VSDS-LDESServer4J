package be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.http.services.EventStreamResponseConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.http.valueobjects.EventStreamResponse;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.RDFDataMgr;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EventStreamResponseConverterTest {
	private final EventStreamResponseConverter eventStreamConverter = new EventStreamResponseConverter();

	@ParameterizedTest
	@ArgumentsSource(ModelsArgumentProvider.class)
	void test_fromModelToEventStreamResponse(Model eventStreamToConvert, Model expectedShape) {
		EventStreamResponse expectedEventStreamResponse = new EventStreamResponse("collectionName1",
				"http://purl.org/dc/terms/created", "http://purl.org/dc/terms/isVersionOf",
				"https://data.vlaanderen.be/ns/mobiliteit#Mobiliteitshinder", List.of(), expectedShape);

		assertEquals(expectedEventStreamResponse, eventStreamConverter.fromModel(eventStreamToConvert));
	}

	@ParameterizedTest
	@ArgumentsSource(ModelsArgumentProvider.class)
	void test_fromEventStreamResponseToModel(Model expectedEventStream, Model shacl) {
		final EventStreamResponse eventStream = new EventStreamResponse("collectionName1",
				"http://purl.org/dc/terms/created", "http://purl.org/dc/terms/isVersionOf",
				"https://data.vlaanderen.be/ns/mobiliteit#Mobiliteitshinder", List.of(), shacl);
		final Model convertedModel = eventStreamConverter.toModel(eventStream);

		assertTrue(expectedEventStream.isIsomorphicWith(convertedModel));
	}

	static class ModelsArgumentProvider implements ArgumentsProvider {
		private Model shacl;

		@Override
		public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) throws Exception {
			// TODO: add ldes with views to stream of arguments
			return Stream.of(
					// Arguments.of(
					// readModelFromFile("eventstream/streams/ldes-with-named-views.ttl"),
					// getShacl()),
					// Arguments.of(
					// readModelFromFile("eventstream/streams/ldes-with-named-view.ttl"),
					// getShacl()),
					Arguments.of(
							readModelFromFile("eventstream/streams/ldes-empty.ttl"),
							getShacl()));
		}

		private Model getShacl() throws URISyntaxException {
			if (shacl == null) {
				shacl = readModelFromFile("eventstream/streams/example-shape.ttl");
			}
			return shacl;
		}

		private Model readModelFromFile(String fileName) throws URISyntaxException {
			ClassLoader classLoader = getClass().getClassLoader();
			String uri = Objects.requireNonNull(classLoader.getResource(fileName)).toURI().toString();
			return RDFDataMgr.loadModel(uri);
		}
	}

}