package be.vlaanderen.informatievlaanderen.ldes.server.retention.repositories;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.EventStreamCreatedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.EventStreamDeletedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.MissingEventStreamException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.EventStream;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.valueobjects.EventStreamProperties;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EventStreamCollectionImplTest {

	private final static String COLLECTION = "COLLECTION";
	private final static String TIMESTAMP_PATH = "http://www.w3.org/ns/prov#generatedAtTime";
	private final static String VERSION_OF_PATH = "http://purl.org/dc/terms/isVersionOf";
	private final EventStreamCollectionImpl eventStreamCollection = new EventStreamCollectionImpl();

	@Test
	void when_EventStreamDoesNotExist_then_MissingEventStreamExceptionIsThrown() {
		eventStreamCollection.handleEventStreamCreatedEvent(
				new EventStreamCreatedEvent(new EventStream(COLLECTION, TIMESTAMP_PATH, VERSION_OF_PATH, "")));
		eventStreamCollection.handleEventStreamDeletedEvent(new EventStreamDeletedEvent(COLLECTION));

		MissingEventStreamException missingEventStreamException = assertThrows(MissingEventStreamException.class,
				() -> eventStreamCollection.getEventStreamProperties(COLLECTION));
		assertEquals("No event stream found for collection COLLECTION", missingEventStreamException.getMessage());
	}

	@Test
	void when_EventStreamExists_then_EventStreamPropertiesCanBeRetrieved() {
		eventStreamCollection.handleEventStreamCreatedEvent(
				new EventStreamCreatedEvent(new EventStream(COLLECTION, TIMESTAMP_PATH, VERSION_OF_PATH, "")));

		EventStreamProperties actualEventStreamProperties = eventStreamCollection.getEventStreamProperties(COLLECTION);

		EventStreamProperties expectedEventStreamProperties = new EventStreamProperties(VERSION_OF_PATH,
				TIMESTAMP_PATH);
		assertEquals(expectedEventStreamProperties, actualEventStreamProperties);
	}

}
