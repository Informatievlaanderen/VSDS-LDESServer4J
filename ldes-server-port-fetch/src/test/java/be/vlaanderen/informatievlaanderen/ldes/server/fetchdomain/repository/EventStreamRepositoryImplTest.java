package be.vlaanderen.informatievlaanderen.ldes.server.fetchdomain.repository;

import be.vlaanderen.informatievlaanderen.ldes.server.fetchdomain.entities.EventStream;
import be.vlaanderen.informatievlaanderen.ldes.server.fetchdomain.valueobjects.EventStreamProperties;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EventStreamRepositoryImplTest {

	private static final String COLLECTION = "collection";
	private final EventStreamRepository eventStreamRepository = new EventStreamRepositoryImpl();

	@Test
	void when_EventStreamIsSaved_then_ItCanBeRetrievedAndDeleted() {
		EventStream eventStream = new EventStream(COLLECTION, new EventStreamProperties("id", "path", "version"));

		eventStreamRepository.saveEventStream(eventStream);
		EventStream actualEventStream = eventStreamRepository.getEventStreamByCollection(COLLECTION);
		assertEquals(actualEventStream, eventStream);
		eventStreamRepository.deleteEventStreamByCollection(COLLECTION);
		assertNull(eventStreamRepository.getEventStreamByCollection(COLLECTION));
	}

}