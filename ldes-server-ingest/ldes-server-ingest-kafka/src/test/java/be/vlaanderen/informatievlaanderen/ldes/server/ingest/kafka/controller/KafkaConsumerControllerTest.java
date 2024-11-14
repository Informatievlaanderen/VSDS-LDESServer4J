package be.vlaanderen.informatievlaanderen.ldes.server.ingest.kafka.controller;

import be.vlaanderen.informatievlaanderen.ldes.server.ingest.kafka.KafkaListenerContainerManager;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.kafka.model.KafkaConsumerRequest;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.kafka.model.KafkaConsumerResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.listener.MessageListenerContainer;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class KafkaConsumerControllerTest {

    private KafkaListenerContainerManager kafkaListenerContainerManager;
    private KafkaConsumerController kafkaConsumerController;

    @BeforeEach
    void setUp() {
        kafkaListenerContainerManager = mock(KafkaListenerContainerManager.class);
        kafkaConsumerController = new KafkaConsumerController(kafkaListenerContainerManager);
    }

    @Test
    void create() throws NoSuchMethodException {
        KafkaConsumerRequest request = new KafkaConsumerRequest("collection", "topic", "mimeType");
        kafkaConsumerController.create(request);
        verify(kafkaListenerContainerManager, times(1)).registerListener(anyString(), eq("collection"), eq("topic"), eq("mimeType"));
    }

    @Test
    void list() {
        when(kafkaListenerContainerManager.listContainers()).thenReturn(Collections.emptyList());
        List<KafkaConsumerResponse> response = kafkaConsumerController.list();
        assertTrue(response.isEmpty());
    }

    @Test
    void get() {
        MessageListenerContainer container = mock(MessageListenerContainer.class);
        when(container.getListenerId()).thenReturn(UUID.randomUUID().toString());
        when(kafkaListenerContainerManager.getContainer(anyString())).thenReturn(Optional.of(container));
        KafkaConsumerResponse response = kafkaConsumerController.get("listenerId");
        assertNotNull(response);
    }

    @Test
    void activate() {
        MessageListenerContainer container = mock(MessageListenerContainer.class);
        when(container.isRunning()).thenReturn(false);
        when(kafkaListenerContainerManager.getContainer(anyString())).thenReturn(Optional.of(container));
        kafkaConsumerController.activate("listenerId");
        verify(container, times(1)).start();
    }

    @Test
    void pause() {
        MessageListenerContainer container = mock(MessageListenerContainer.class);
        when(container.isRunning()).thenReturn(true);
        when(container.isContainerPaused()).thenReturn(false);
        when(container.isPauseRequested()).thenReturn(false);
        when(kafkaListenerContainerManager.getContainer(anyString())).thenReturn(Optional.of(container));
        kafkaConsumerController.pause("listenerId");
        verify(container, times(1)).pause();
    }

    @Test
    void resume() {
        MessageListenerContainer container = mock(MessageListenerContainer.class);
        when(container.isRunning()).thenReturn(true);
        when(container.isContainerPaused()).thenReturn(true);
        when(kafkaListenerContainerManager.getContainer(anyString())).thenReturn(Optional.of(container));
        kafkaConsumerController.resume("listenerId");
        verify(container, times(1)).resume();
    }

    @Test
    void stop() {
        MessageListenerContainer container = mock(MessageListenerContainer.class);
        when(container.isRunning()).thenReturn(true);
        when(kafkaListenerContainerManager.getContainer(anyString())).thenReturn(Optional.of(container));
        kafkaConsumerController.stop("listenerId");
        verify(container, times(1)).stop();
    }

    @Test
    void delete() {
        MessageListenerContainer container = mock(MessageListenerContainer.class);
        when(kafkaListenerContainerManager.getContainer(anyString())).thenReturn(Optional.of(container));
        kafkaConsumerController.delete("listenerId");
        verify(container, times(1)).stop();
        verify(kafkaListenerContainerManager, times(1)).unregisterListener("listenerId");
    }
}