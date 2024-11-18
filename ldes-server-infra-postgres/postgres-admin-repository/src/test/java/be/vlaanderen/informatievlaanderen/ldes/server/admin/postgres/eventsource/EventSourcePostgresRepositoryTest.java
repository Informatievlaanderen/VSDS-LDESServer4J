package be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.eventsource;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.eventsource.entity.EventSourceEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.eventsource.repository.EventSourceEntityRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.eventstream.entity.EventStreamEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.eventstream.repository.EventStreamEntityRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.EventSource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventSourcePostgresRepositoryTest {
    private static final String COLLECTION_NAME = "testCollection";
    private static final EventSource EVENT_SOURCE = new EventSource(COLLECTION_NAME, List.of());
    private static final EventSourceEntity EVENT_SOURCE_ENTITY = new EventSourceEntity(
            new EventStreamEntity(COLLECTION_NAME, "", "", null, false, null)
    );

    @Mock
    private EventSourceEntityRepository eventSourceEntityRepository;
    @Mock
    private EventStreamEntityRepository eventStreamEntityRepository;
    @InjectMocks
    private EventSourcePostgresRepository eventSourcePostgresRepository;

    @Test
    void given_ValidEventSource_when_SaveEventSource_then_EventSourceIsSaved() {
        when(eventSourceEntityRepository.findByCollectionName(COLLECTION_NAME)).thenReturn(Optional.empty());
        when(eventStreamEntityRepository.findByName(COLLECTION_NAME)).thenReturn(Optional.of(mock()));

        eventSourcePostgresRepository.saveEventSource(EVENT_SOURCE);

        verify(eventSourceEntityRepository).save(any(EventSourceEntity.class));
    }

    @Test
    void given_CollectionName_when_GetEventSource_then_ReturnsEventSource() {
        when(eventSourceEntityRepository.findByCollectionName(COLLECTION_NAME)).thenReturn(Optional.of(EVENT_SOURCE_ENTITY));

        Optional<EventSource> result = eventSourcePostgresRepository.getEventSource(COLLECTION_NAME);

        assertThat(result).contains(EVENT_SOURCE); // assuming you have equals method properly implemented in EventSource
    }

    @Test
    void given_EmptyRepository_when_GetAllEventSources_then_ReturnEmptyList() {
        final var result = eventSourcePostgresRepository.getAllEventSources();

        assertThat(result).isEmpty();
    }

    @Test
    void given_MultipleEventSources_when_GetAllEventSources_then_ReturnList() {
        final String otherCollectionName = "other-collection";
        final EventSourceEntity secondEntity = mock();
        when(secondEntity.getCollectionName()).thenReturn(otherCollectionName);
        when(eventSourceEntityRepository.findAll()).thenReturn(List.of(EVENT_SOURCE_ENTITY, secondEntity));

        final var result = eventSourcePostgresRepository.getAllEventSources();

        assertThat(result)
                .map(EventSource::collectionName)
                .containsExactlyInAnyOrder(COLLECTION_NAME, otherCollectionName);
    }

    @Test
    void given_NonExistingEventSource_when_GetEventSource_then_ReturnEmptyOptional() {
        when(eventSourceEntityRepository.findByCollectionName(COLLECTION_NAME)).thenReturn(Optional.empty());

        final var result = eventSourcePostgresRepository.getEventSource(COLLECTION_NAME);

        assertThat(result).isEmpty();
    }

    @Test
    void given_EventSourceExists_when_GetEventSource_then_ReturnEventSource() {
        when(eventSourceEntityRepository.findByCollectionName(COLLECTION_NAME)).thenReturn(Optional.of(EVENT_SOURCE_ENTITY));

        final var result = eventSourcePostgresRepository.getEventSource(COLLECTION_NAME);

        assertThat(result).contains(EVENT_SOURCE);
    }
}
