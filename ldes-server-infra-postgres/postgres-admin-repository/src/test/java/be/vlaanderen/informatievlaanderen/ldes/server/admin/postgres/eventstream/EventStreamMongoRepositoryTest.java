package be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.eventstream;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.eventstream.entity.EventStreamEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.eventstream.repository.EventStreamEntityRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.EventStream;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventStreamMongoRepositoryTest {
	private static final String COLLECTION_NAME = "collection1";
	private static final EventStreamEntity EVENT_STREAM_ENTITY = new EventStreamEntity(COLLECTION_NAME, "generatedAt",
			"isVersionOf", false);
	private static final EventStream EVENT_STREAM = new EventStream(COLLECTION_NAME, "generatedAt", "isVersionOf", false);
	private EventStreamPostgresRepository repository;
	@Mock
	private EventStreamEntityRepository eventStreamEntityRepository;
	@Mock
	EntityManager entityManager;

	@BeforeEach
	void setUp() {
		repository = new EventStreamPostgresRepository(eventStreamEntityRepository);
	}

	@Test
	@DisplayName("test retrieval of all eventstreams of a non-empty mongo collection")
	void when_dbHasEntities_then_returnAll() {
		when(eventStreamEntityRepository.findAll()).thenReturn(List.of(
				EVENT_STREAM_ENTITY,
				new EventStreamEntity("other_collection", "created", "version", false)));

		List<EventStream> eventStreams = repository.retrieveAllEventStreams();
		List<EventStream> expectedEventStreams = List.of(
				EVENT_STREAM,
				new EventStream("other_collection", "created", "version", false));
		verify(eventStreamEntityRepository).findAll();
		assertEquals(expectedEventStreams, eventStreams);
	}

	@Test
	void when_dbIsEmpty_then_returnEmptyList() {
		when(eventStreamEntityRepository.findAll()).thenReturn(List.of());

		List<EventStream> eventStreams = repository.retrieveAllEventStreams();

		verify(eventStreamEntityRepository).findAll();
		assertTrue(eventStreams.isEmpty());
	}

	@Test
	void when_singleEventStreamQueried() {
		when(eventStreamEntityRepository.findById(COLLECTION_NAME)).thenReturn(Optional.of(EVENT_STREAM_ENTITY));

		Optional<EventStream> eventStream = repository.retrieveEventStream(COLLECTION_NAME);

		verify(eventStreamEntityRepository).findById(COLLECTION_NAME);
		assertTrue(eventStream.isPresent());
		assertEquals(EVENT_STREAM, eventStream.get());
	}

	@Test
	void when_emptyDbQueried_then_returnEmptyOptional() {
		final String collection = "other";
		when(eventStreamEntityRepository.findById(collection)).thenReturn(Optional.empty());

		Optional<EventStream> eventStream = repository.retrieveEventStream(collection);

		verify(eventStreamEntityRepository).findById(collection);
		assertTrue(eventStream.isEmpty());
	}

	@Test
	void test_insertion() {
		EventStream savedEventStream = repository.saveEventStream(EVENT_STREAM);

		verify(eventStreamEntityRepository).save(any(EventStreamEntity.class));
		assertEquals(EVENT_STREAM, savedEventStream);
	}

	@Test
	void test_deletion() {
		repository.deleteEventStream(COLLECTION_NAME);

		verify(eventStreamEntityRepository).deleteById(COLLECTION_NAME);
	}
}