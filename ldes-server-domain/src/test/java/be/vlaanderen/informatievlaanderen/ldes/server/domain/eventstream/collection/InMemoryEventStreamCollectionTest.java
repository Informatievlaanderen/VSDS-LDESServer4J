package be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.collection;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.repository.EventStreamRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.entities.EventStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InMemoryEventStreamCollectionTest {
	private static final String COLLECTION_NAME = "collection1";
	private static final EventStream EVENT_STREAM = new EventStream(COLLECTION_NAME, "generatedAt", "isVersionOf", memberType);
	@Mock
	private EventStreamRepository repository;
	private EventStreamCollection eventStreamCollection;

	@BeforeEach
	void setUp() {
		eventStreamCollection = new InMemoryEventStreamCollection(repository);

	}

	@Test
	void test_postConstruction() {
		when(repository.retrieveAllEventStreams()).thenReturn(List.of(EVENT_STREAM));

		((InMemoryEventStreamCollection) eventStreamCollection).initCollection();

		List<EventStream> eventStreams = eventStreamCollection.retrieveAllEventStreams();
		assertTrue(eventStreams.contains(EVENT_STREAM));
	}

	@Test
	void test_insertionAndRetrieval() {
		eventStreamCollection.saveEventStream(EVENT_STREAM);

		verify(repository).saveEventStream(EVENT_STREAM);

		Optional<EventStream> eventStream = eventStreamCollection.retrieveEventStream(COLLECTION_NAME);

		assertTrue(eventStream.isPresent());
		assertEquals(EVENT_STREAM, eventStream.get());
	}

	@Test
	void test_deletion() {
		eventStreamCollection.saveEventStream(EVENT_STREAM);

		assertTrue(eventStreamCollection.retrieveEventStream(COLLECTION_NAME).isPresent());

		eventStreamCollection.deleteEventStream(COLLECTION_NAME);

		verify(repository).deleteEventStream(COLLECTION_NAME);
		assertTrue(eventStreamCollection.retrieveEventStream(COLLECTION_NAME).isEmpty());
	}
}