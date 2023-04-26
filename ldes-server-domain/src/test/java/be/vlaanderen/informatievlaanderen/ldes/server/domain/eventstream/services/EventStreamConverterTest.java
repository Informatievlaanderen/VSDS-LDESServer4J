package be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.valueobjects.EventStream;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.riot.RDFDataMgr;
import org.junit.jupiter.api.Test;

import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.LDES;
import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.TREE_SHAPE;
import static org.junit.jupiter.api.Assertions.*;

class EventStreamConverterTest {
	private final EventStreamConverter eventStreamConverter = new EventStreamConverter();

	@Test
	void when_modelReceived_then_eventStreamIsReturned() throws URISyntaxException {
		Model model = readModelFromFile("eventstream/streams/ldes-empty.ttl");

		EventStream expectedEventStream = new EventStream("collectionName1", "generatedAt", "1.1", List.of());

		assertEquals(expectedEventStream, eventStreamConverter.fromModel(model));
	}

	@Test
	void when_eventStreamReceived_then_modelIsReturned() throws URISyntaxException {
		EventStream eventStream = new EventStream("collectionName1", "generatedAt", "1.1", List.of());

		Model includedShape = readModelFromFile("eventstream/streams/example-shape.ttl");
		Statement statement = includedShape.createStatement(ResourceFactory.createResource(LDES + eventStream.getCollection()), TREE_SHAPE, ResourceFactory.createResource(LDES + "shape"));
		includedShape.add(statement);
		Model expectedModel = readModelFromFile("eventstream/streams/ldes-empty.ttl");

		Model convertedModel = eventStreamConverter.toModel(eventStream);
		convertedModel.add(includedShape);

		assertTrue(expectedModel.isIsomorphicWith(convertedModel));
	}

	private Model readModelFromFile(String fileName) throws URISyntaxException{
		ClassLoader classLoader = getClass().getClassLoader();
		String uri = Objects.requireNonNull(classLoader.getResource(fileName)).toURI().toString();
		return RDFDataMgr.loadModel(uri);
	}
}