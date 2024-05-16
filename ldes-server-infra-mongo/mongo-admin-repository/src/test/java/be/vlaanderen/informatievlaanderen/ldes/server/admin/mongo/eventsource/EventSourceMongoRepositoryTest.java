package be.vlaanderen.informatievlaanderen.ldes.server.admin.mongo.eventsource;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.mongo.eventsource.entity.EventSourceEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.mongo.eventsource.repository.EventSourceEntityRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.mongo.eventsource.service.EventSourceEntityConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.RdfModelConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.RetentionModelSerializer;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.EventSource;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.Lang;
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
class EventSourceMongoRepositoryTest {
    private static final String COLLECTION_NAME = "collection1";
    private static final List<Model> RETENTION_MODELS = List.of(
            ModelFactory.createDefaultModel()
    );
    private static final EventSource EVENT_SOURCE = new EventSource(COLLECTION_NAME, RETENTION_MODELS);
    private EventSourceEntity eventSourceEntity;
    private RdfModelConverter modelConverter = new RdfModelConverter();
    private RetentionModelSerializer serializer = new RetentionModelSerializer(modelConverter);
    private EventSourceEntityConverter converter = new EventSourceEntityConverter(serializer);
    private EventSourceMongoRepository mongoRepository;
    @Mock
    private EventSourceEntityRepository eventSourceEntityRepository;

    @BeforeEach
    void setUp() {
        List<String> modelsStrings = RETENTION_MODELS.stream().map(model -> RdfModelConverter.toString(model, Lang.TURTLE)).toList();
        eventSourceEntity = new EventSourceEntity(COLLECTION_NAME, modelsStrings);
        mongoRepository = new EventSourceMongoRepository(eventSourceEntityRepository, converter);
    }

    @Test
    @DisplayName("test retrieval of all eventsources of a non-empty mongo collection")
    void when_dbHasEntities_then_returnAll() {
        when(eventSourceEntityRepository.findAll()).thenReturn(List.of(
                eventSourceEntity,
                new EventSourceEntity("col2", List.of())));

        List<EventSource> eventSources = mongoRepository.getAllEventSources();
        List<EventSource> expectedEventSources = List.of(
                EVENT_SOURCE,
                new EventSource("col2", List.of()));
        verify(eventSourceEntityRepository).findAll();
        assertEquals(expectedEventSources, eventSources);
    }

    @Test
    void when_dbIsEmpty_then_returnEmptyList() {
        when(eventSourceEntityRepository.findAll()).thenReturn(List.of());

        List<EventSource> eventStreams = mongoRepository.getAllEventSources();

        verify(eventSourceEntityRepository).findAll();
        assertTrue(eventStreams.isEmpty());
    }

    @Test
    void when_singleEventStreamQueried() {
        when(eventSourceEntityRepository.findById(COLLECTION_NAME)).thenReturn(Optional.of(eventSourceEntity));

        Optional<EventSource> eventSource = mongoRepository.getEventSource(COLLECTION_NAME);

        verify(eventSourceEntityRepository).findById(COLLECTION_NAME);
        assertTrue(eventSource.isPresent());
        assertEquals(EVENT_SOURCE, eventSource.get());
    }

    @Test
    void test_insertion() {
        mongoRepository.saveEventSource(EVENT_SOURCE);

        verify(eventSourceEntityRepository).save(any(EventSourceEntity.class));
    }
}