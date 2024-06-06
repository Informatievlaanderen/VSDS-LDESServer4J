package be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.eventstream;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.eventstream.repository.EventStreamEntityRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.eventstream.v2.entity.EventStreamEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.EventStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventStreamPostgresRepositoryTest {
    private static final String COLLECTION_NAME = "collection-name";
    private static final String COLLECTION_1 = "collection1";
    private static final be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.eventstream.entity.EventStreamEntity OLD_EVENT_STREAM_ENTITY = new be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.eventstream.entity.EventStreamEntity(COLLECTION_1, "generatedAt",
            "isVersionOf", false);
    private static final EventStream EVENT_STREAM_1 = new EventStream(COLLECTION_1, "generatedAt", "isVersionOf", false);
    private static final EventStream EVENT_STREAM = new EventStream(COLLECTION_NAME, "timestampPath", "versionOfPath", false);
    private static final EventStreamEntity EVENT_STREAM_ENTITY = new EventStreamEntity(COLLECTION_NAME, "timestampPath", "versionOfPath", false, false);
    private EventStreamPostgresRepository repository;
    @Mock
    private EventStreamEntityRepository eventStreamEntityRepository;
    @Mock
    private be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.eventstream.v2.repository.EventStreamEntityRepository newEventStreamEntityRepository;

    @BeforeEach
    void setUp() {
        repository = new EventStreamPostgresRepository(eventStreamEntityRepository, newEventStreamEntityRepository);
    }

    @Test
    @DisplayName("test retrieval of all eventstreams of a non-empty mongo collection")
    void when_dbHasEntities_then_returnAll() {
        when(eventStreamEntityRepository.findAll()).thenReturn(List.of(OLD_EVENT_STREAM_ENTITY));
        when(newEventStreamEntityRepository.findAll()).thenReturn(List.of(
                EVENT_STREAM_ENTITY,
                new EventStreamEntity("other_collection", "created", "version", false, false)
        ));

        List<EventStream> eventStreams = repository.retrieveAllEventStreams();
        List<EventStream> expectedEventStreams = List.of(
                EVENT_STREAM,
                EVENT_STREAM_1,
                new EventStream("other_collection", "created", "version", false));
        assertThat(eventStreams).containsExactlyInAnyOrderElementsOf(expectedEventStreams);
    }

    @Test
    void when_dbIsEmpty_then_returnEmptyList() {
        when(eventStreamEntityRepository.findAll()).thenReturn(List.of());
        when(newEventStreamEntityRepository.findAll()).thenReturn(List.of());

        List<EventStream> eventStreams = repository.retrieveAllEventStreams();

        assertThat(eventStreams).isEmpty();
    }

    @Test
    void when_OldSingleEventStreamQueried() {
        when(eventStreamEntityRepository.findById(COLLECTION_1)).thenReturn(Optional.of(OLD_EVENT_STREAM_ENTITY));

        Optional<EventStream> eventStream = repository.retrieveEventStream(COLLECTION_1);

        assertThat(eventStream).contains(EVENT_STREAM_1);
    }

    @Test
    void when_singleEventStreamQueried() {
        when(newEventStreamEntityRepository.findByName(COLLECTION_NAME)).thenReturn(Optional.of(EVENT_STREAM_ENTITY));

        Optional<EventStream> eventStream = repository.retrieveEventStream(COLLECTION_NAME);

        assertThat(eventStream).contains(EVENT_STREAM);
    }

    @Test
    void when_emptyDbQueried_then_returnEmptyOptional() {
        final String collection = "other";

        Optional<EventStream> eventStream = repository.retrieveEventStream(collection);

        verify(eventStreamEntityRepository).findById(collection);
        verify(newEventStreamEntityRepository).findByName(collection);
        assertThat(eventStream).isEmpty();
    }

    @Test
    void test_insertion() {
        EventStream savedEventStream = repository.saveEventStream(EVENT_STREAM);

        verify(eventStreamEntityRepository).save(any());
        verify(newEventStreamEntityRepository).save(any());
        assertThat(savedEventStream).isEqualTo(EVENT_STREAM);
    }

    @Test
    void test_deletion() {
        repository.deleteEventStream(COLLECTION_1);

        verify(eventStreamEntityRepository).deleteById(COLLECTION_1);
        verify(newEventStreamEntityRepository).deleteByName(COLLECTION_1);
    }
}