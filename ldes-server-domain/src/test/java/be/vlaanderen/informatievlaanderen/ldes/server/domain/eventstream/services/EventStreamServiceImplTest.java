package be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.collection.EventStreamCollection;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.entities.EventStream;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.MissingEventStreamException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventStreamServiceImplTest {
	private static final String COLLECTION = "collection";
	private static final EventStream EVENT_STREAM = new EventStream(COLLECTION, "generatedAt", "isVersionOf", memberType);
	@Mock
	private EventStreamCollection eventStreamCollection;

	private EventStreamService service;

	@BeforeEach
	void setUp() {
		service = new EventStreamServiceImpl(eventStreamCollection);
	}

	@Test
	void when_retrieveAllEventStream_then_returnList() {
		EventStream other = new EventStream("other", "created", "versionOf", memberType);

		when(eventStreamCollection.retrieveAllEventStreams()).thenReturn(List.of(EVENT_STREAM, other));
		List<EventStream> eventStreams = service.retrieveAllEventStreams();
		List<EventStream> expectedEventStreams = List.of(EVENT_STREAM, other);

		verify(eventStreamCollection).retrieveAllEventStreams();
		assertEquals(expectedEventStreams, eventStreams);
	}

	@Test
	void when_collectionExists_then_retrieveShape() {
		when(eventStreamCollection.retrieveEventStream(COLLECTION)).thenReturn(Optional.of(EVENT_STREAM));
		assertEquals(EVENT_STREAM, service.retrieveEventStream(COLLECTION));
	}

	@Test
	void when_collectionDoesNotExists_then_throwException() {
		when(eventStreamCollection.retrieveEventStream(COLLECTION)).thenReturn(Optional.empty());

		Exception e = assertThrows(MissingEventStreamException.class, () -> service.retrieveEventStream(COLLECTION));
		assertEquals("No event stream found for collection " + COLLECTION, e.getMessage());
	}

	@Test
	void when_collectionExists_and_updateEventStream_then_expectUpdatedEventStream() {
		EventStream eventStream = new EventStream(COLLECTION, "generatedAt", "versionOf", memberType);

		when(eventStreamCollection.saveEventStream(eventStream)).thenReturn(eventStream);

		EventStream updatedEventStream = service.saveEventStream(eventStream);

		verify(eventStreamCollection).saveEventStream(eventStream);
		assertEquals(eventStream, updatedEventStream);
	}

	@Test
	void when_collectionDoesNotExists_and_triesToDelete_then_throwException() {
		when(eventStreamCollection.retrieveEventStream(COLLECTION)).thenReturn(Optional.empty());
		Exception e = assertThrows(MissingEventStreamException.class, () -> service.deleteEventStream(COLLECTION));
		assertEquals("No event stream found for collection " + COLLECTION, e.getMessage());
	}

	@Test
	void when_collectionExists_and_triesToDeleteEventStream_then_throwExceptionWithRetrieval() {
		when(eventStreamCollection.retrieveEventStream(COLLECTION)).thenReturn(Optional.of(EVENT_STREAM)).thenReturn(Optional.empty());

		service.deleteEventStream(COLLECTION);

		verify(eventStreamCollection).deleteEventStream(COLLECTION);
		assertThrows(MissingEventStreamException.class, () -> service.retrieveEventStream(COLLECTION));
	}
}