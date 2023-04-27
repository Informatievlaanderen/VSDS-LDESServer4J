package be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.http.services.EventStreamHttpMessageConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.http.services.EventStreamHttpMessageConverterImpl;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.http.valueobjects.EventStreamHttpMessage;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.RDFDataMgr;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.net.URISyntaxException;
import java.util.Objects;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EventStreamHttpMessageConverterImplTest {
	private final EventStreamHttpMessageConverter eventStreamConverter = new EventStreamHttpMessageConverterImpl();

	@ParameterizedTest
	@ArgumentsSource(ModelsArgumentProvider.class)
	void when_modelReceived_then_eventStreamIsReturned(Model eventStreamToConvert, Model expectedViews,
			Model expectedShape) {
		EventStreamHttpMessage expectedEventStreamHttpMessage = new EventStreamHttpMessage("collectionName1",
				"generatedAt", "1.1", expectedViews, expectedShape);

		assertEquals(expectedEventStreamHttpMessage, eventStreamConverter.fromModel(eventStreamToConvert));
	}

	@ParameterizedTest
	@ArgumentsSource(ModelsArgumentProvider.class)
	void when_eventStreamReceived_then_modelIsReturned(Model expectedEventStream, Model views, Model shacl)
			throws URISyntaxException {
		final EventStreamHttpMessage eventStream = new EventStreamHttpMessage("collectionName1", "generatedAt", "1.1",
				views, shacl);
		final Model convertedModel = eventStreamConverter.toModel(eventStream);

		assertTrue(expectedEventStream.isIsomorphicWith(convertedModel));
	}

	static class ModelsArgumentProvider implements ArgumentsProvider {
		private Model shacl;

		@Override
		public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) throws Exception {
			return Stream.of(
					Arguments.of(
							readModelFromFile("eventstream/streams/ldes-with-named-views.ttl"),
							readModelFromFile("eventstream/streams/views.ttl"),
							getShacl()),
					Arguments.of(
							readModelFromFile("eventstream/streams/ldes-with-named-view.ttl"),
							readModelFromFile("eventstream/streams/view.ttl"),
							getShacl()),
					Arguments.of(
							readModelFromFile("eventstream/streams/ldes-empty.ttl"),
							ModelFactory.createDefaultModel(),
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