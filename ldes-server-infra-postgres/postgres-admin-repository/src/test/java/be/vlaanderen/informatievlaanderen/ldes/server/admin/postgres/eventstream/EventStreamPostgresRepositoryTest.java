package be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.eventstream;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.eventsource.entity.EventSourceEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.eventstream.entity.EventStreamEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.eventstream.projection.EventStreamProperties;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.eventstream.repository.EventStreamEntityRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.shaclshape.entity.ShaclShapeEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.view.entity.ViewEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.spi.EventStreamTO;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.EventStream;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.VersionCreationProperties;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewSpecification;
import org.apache.jena.rdf.model.ModelFactory;
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
import static org.mockito.ArgumentMatchers.assertArg;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventStreamPostgresRepositoryTest {
    private static final String COLLECTION_NAME = "collection-name";
    private static final String OTHER_COLLECTION_NAME = "other-collection";
    private static final String TIMESTAMP_PATH = "timestampPath";
    private static final String VERSION_OF_PATH = "versionOfPath";
    private static final String SKOLEMIZATION_DOMAIN = "http://example.org";
    private static final EventStream EVENT_STREAM = new EventStream(COLLECTION_NAME, TIMESTAMP_PATH, VERSION_OF_PATH, VersionCreationProperties.disabled(), SKOLEMIZATION_DOMAIN);
    private static final EventStreamTO EVENT_STREAM_TO = new EventStreamTO.Builder().withEventStream(EVENT_STREAM).withShacl(ModelFactory.createDefaultModel()).build();
    private static final EventStreamEntity EVENT_STREAM_ENTITY = createEventStreamEntity(COLLECTION_NAME);
    private static final EventStreamProperties EVENT_STREAM_PROPERTIES = new EventStreamPropertiesTestImpl(COLLECTION_NAME, TIMESTAMP_PATH, VERSION_OF_PATH, null, false, SKOLEMIZATION_DOMAIN);
    private EventStreamPostgresRepository repository;

    @Mock
    private EventStreamEntityRepository eventStreamEntityRepository;

    @BeforeEach
    void setUp() {
        repository = new EventStreamPostgresRepository(eventStreamEntityRepository);
    }

    @Test
    @DisplayName("test retrieval of all eventstreams projections of a non-empty collection")
    void when_dbHasEntities_then_returnAllProjections() {
        final EventStreamProperties projection2 = new EventStreamPropertiesTestImpl(
                OTHER_COLLECTION_NAME,
                "created",
                "version",
                null,
                false,
                null
        );
        when(eventStreamEntityRepository.findAllPropertiesBy()).thenReturn(List.of(
                EVENT_STREAM_PROPERTIES,
                projection2
        ));
        final List<EventStream> expectedEventStreams = List.of(
                EVENT_STREAM,
                new EventStream(OTHER_COLLECTION_NAME, "created", "version", VersionCreationProperties.disabled(), null));

        final List<EventStream> eventStreams = repository.retrieveAllEventStreams();

        assertThat(eventStreams).containsExactlyInAnyOrderElementsOf(expectedEventStreams);
    }

    @Test
    @DisplayName("test retrieval of all eventstreams of a non-empty collection")
    void when_dbHasEntities_then_returnAll() {
        final String viewName = "view-name";
        final EventStreamEntity otherEventStreamEntity = createEventStreamEntity(OTHER_COLLECTION_NAME);
        otherEventStreamEntity.setViews(List.of(
                new ViewEntity(viewName, List.of(), List.of(), 250){{
                    setEventStream(otherEventStreamEntity);
                }}
        ));
        when(eventStreamEntityRepository.findAll()).thenReturn(List.of(
                EVENT_STREAM_ENTITY,
                otherEventStreamEntity
        ));
        final List<ViewSpecification> expectedViews = List.of(new ViewSpecification(
                new ViewName(OTHER_COLLECTION_NAME, viewName), List.of(), List.of(), 250
        ));
        final List<EventStreamTO> expectedEventStreams = List.of(
                EVENT_STREAM_TO,
                new EventStreamTO.Builder()
                        .withCollection(OTHER_COLLECTION_NAME)
                        .withTimestampPath(TIMESTAMP_PATH)
                        .withVersionOfPath(VERSION_OF_PATH)
                        .withSkolemizationDomain(SKOLEMIZATION_DOMAIN)
                        .withViews(expectedViews)
                        .withShacl(ModelFactory.createDefaultModel())
                        .build());

        final List<EventStreamTO> eventStreams = repository.retrieveAllEventStreamTOs();

        assertThat(eventStreams).containsExactlyInAnyOrderElementsOf(expectedEventStreams);
    }

    @Test
    @DisplayName("test retrieval of all eventstreams of a non-empty collection")
    void when_dbHasEntitiesInTwoDataModels_then_returnAllOnce() {
        final String otherTimestampPath = "other-timestampPath";
        final String otherVersionOfPath = "other-versionOf-path";
        List<EventStream> expectedEventStreams = List.of(
                EVENT_STREAM,
                new EventStream(OTHER_COLLECTION_NAME, otherTimestampPath, otherVersionOfPath, VersionCreationProperties.disabled(), false, SKOLEMIZATION_DOMAIN)
        );
        when(eventStreamEntityRepository.findAllPropertiesBy()).thenReturn(List.of(
                EVENT_STREAM_PROPERTIES,
                new EventStreamPropertiesTestImpl(OTHER_COLLECTION_NAME, otherTimestampPath, otherVersionOfPath, null, false, SKOLEMIZATION_DOMAIN)
        ));

        List<EventStream> eventStreams = repository.retrieveAllEventStreams();

        assertThat(eventStreams).containsExactlyInAnyOrderElementsOf(expectedEventStreams);
    }

    @Test
    void when_dbIsEmpty_then_returnEmptyPropertiesList() {
        when(eventStreamEntityRepository.findAllPropertiesBy()).thenReturn(List.of());

        List<EventStream> eventStreams = repository.retrieveAllEventStreams();

        assertThat(eventStreams).isEmpty();
    }

    @Test
    void when_dbIsEmpty_then_returnEmptyList() {
        when(eventStreamEntityRepository.findAll()).thenReturn(List.of());

        List<EventStreamTO> eventStreams = repository.retrieveAllEventStreamTOs();

        assertThat(eventStreams).isEmpty();
    }

    @Test
    void when_singleEventStreamPropertiesQueried() {
        when(eventStreamEntityRepository.findPropertiesByName(COLLECTION_NAME)).thenReturn(Optional.of(EVENT_STREAM_PROPERTIES));

        Optional<EventStream> eventStream = repository.retrieveEventStream(COLLECTION_NAME);

        assertThat(eventStream).contains(EVENT_STREAM);
    }

    @Test
    void when_singleEventStreamQueried() {
        when(eventStreamEntityRepository.findByName(COLLECTION_NAME)).thenReturn(Optional.of(EVENT_STREAM_ENTITY));

        Optional<EventStreamTO> eventStream = repository.retrieveEventStreamTO(COLLECTION_NAME);

        assertThat(eventStream).contains(EVENT_STREAM_TO);
    }

    @Test
    void when_emptyDbQueried_then_returnEmptyPropertiesOptional() {
        Optional<EventStreamTO> eventStream = repository.retrieveEventStreamTO(OTHER_COLLECTION_NAME);

        verify(eventStreamEntityRepository).findByName(OTHER_COLLECTION_NAME);
        assertThat(eventStream).isEmpty();
    }

    @Test
    void when_emptyDbQueried_then_returnEmptyOptional() {
        Optional<EventStream> eventStream = repository.retrieveEventStream(OTHER_COLLECTION_NAME);

        verify(eventStreamEntityRepository).findPropertiesByName(OTHER_COLLECTION_NAME);
        assertThat(eventStream).isEmpty();
    }

    @Test
    void test_insertion() {
        final EventStreamEntity expectedEntity = createEventStreamEntity(COLLECTION_NAME);
        when(eventStreamEntityRepository.save(any())).thenReturn(expectedEntity);

        repository.saveEventStream(EVENT_STREAM_TO);

        verify(eventStreamEntityRepository).save(assertArg(entity -> assertThat(entity)
                .usingRecursiveComparison()
                .ignoringFieldsOfTypes(EventStreamEntity.class) // to prevent cycles
                .isEqualTo(expectedEntity)));
    }

    @Test
    void test_deletion() {
        repository.deleteEventStream(OTHER_COLLECTION_NAME);

        verify(eventStreamEntityRepository).deleteByName(OTHER_COLLECTION_NAME);
    }

    private static EventStreamEntity createEventStreamEntity(String collection) {
        final EventStreamEntity eventStreamEntity = new EventStreamEntity(
                collection,
                TIMESTAMP_PATH,
                VERSION_OF_PATH,
                null,
                false,
                SKOLEMIZATION_DOMAIN
        );
        eventStreamEntity.setId(1);
        eventStreamEntity.setShaclShapeEntity(new ShaclShapeEntity(eventStreamEntity, ModelFactory.createDefaultModel()));
        eventStreamEntity.setViews(List.of());
        eventStreamEntity.setEventSourceEntity(new EventSourceEntity(eventStreamEntity, List.of()));
        return eventStreamEntity;
    }
}