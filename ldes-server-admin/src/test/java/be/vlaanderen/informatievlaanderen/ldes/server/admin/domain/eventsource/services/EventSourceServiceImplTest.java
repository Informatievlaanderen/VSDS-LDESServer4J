package be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.eventsource.services;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.eventsource.repository.EventSourceRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.DeletionPolicyChangedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.EventSource;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.event.ApplicationEventMulticaster;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventSourceServiceImplTest {
    private static final String COLLECTION_NAME = "collection";
    private List<Model> retentionModels;
    private EventSourceService eventSourceService;
    private EventSource eventSource;
    @Mock
    private EventSourceRepository eventSourceRepository;
    @Mock
    private ApplicationEventMulticaster eventPublisher;
    @Captor
    ArgumentCaptor<EventSource> eventSourceArgumentCaptor;

    @BeforeEach
    void setUp() {
        retentionModels = List.of(ModelFactory.createDefaultModel());
        eventSource = new EventSource(COLLECTION_NAME, retentionModels);
        eventSourceService = new EventSourceServiceImpl(eventSourceRepository, eventPublisher);
    }

    @Test
    void when_SaveEventSource_Then_EventSourceSaved() {
        eventSourceService.saveEventSource(COLLECTION_NAME, retentionModels);

        InOrder inOrder = inOrder(eventSourceRepository, eventPublisher);
        inOrder.verify(eventSourceRepository).saveEventSource(eventSourceArgumentCaptor.capture());
        inOrder.verify(eventPublisher).multicastEvent(any(DeletionPolicyChangedEvent.class));
        inOrder.verifyNoMoreInteractions();
        assertThat(eventSourceArgumentCaptor.getValue())
                .hasFieldOrPropertyWithValue("collectionName", COLLECTION_NAME)
                .hasFieldOrPropertyWithValue("retentionPolicies", retentionModels);
    }

    @Test
    void when_GetEventSource_Then_EventSourceIsRetrieved() {
        when(eventSourceRepository.getEventSource(COLLECTION_NAME))
                .thenReturn(Optional.of(eventSource));

        Optional<EventSource> actual = eventSourceService.getEventSource(COLLECTION_NAME);

        InOrder inOrder = inOrder(eventSourceRepository);
        inOrder.verify(eventSourceRepository).getEventSource(COLLECTION_NAME);
        inOrder.verifyNoMoreInteractions();
        assertThat(actual.get())
                .hasFieldOrPropertyWithValue("collectionName", COLLECTION_NAME)
                .hasFieldOrPropertyWithValue("retentionPolicies", retentionModels);
    }
}