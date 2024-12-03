package be.vlaanderen.informatievlaanderen.ldes.server.ingest.kafka.controller;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.KafkaSourceDeletedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.kafka.KafkaListenerContainerManager;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.kafka.exception.KafkaConsumerException;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.kafka.model.KafkaConsumerAssignment;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.kafka.model.KafkaConsumerRequest;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.kafka.model.KafkaConsumerResponse;
import org.apache.kafka.common.TopicPartition;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping(path = "/consumers")
public class KafkaConsumerController {
	private final KafkaListenerContainerManager kafkaListenerContainerManager;
	private final ApplicationEventPublisher eventPublisher;

	public KafkaConsumerController(KafkaListenerContainerManager kafkaListenerContainerManager, ApplicationEventPublisher eventPublisher) {
		this.kafkaListenerContainerManager = kafkaListenerContainerManager;
		this.eventPublisher = eventPublisher;
	}

	@PostMapping
	public void create(@RequestBody KafkaConsumerRequest req) throws NoSuchMethodException {
		kafkaListenerContainerManager.registerListener(
				UUID.randomUUID().toString(),
				req.collection(),
				req.topic(),
				req.mimeType()
		);
	}

	@GetMapping
	public List<KafkaConsumerResponse> list() {
		return kafkaListenerContainerManager.listContainers()
				.stream()
				.map(this::createKafkaConsumerResponse)
				.toList();
	}

	@GetMapping(path="/{listenerId}")
	public KafkaConsumerResponse get(@PathVariable String listenerId) {
		return createKafkaConsumerResponse(getListenerContainer(listenerId));
	}

	@PutMapping(path = "/{listenerId}/activate")
	public void activate(@PathVariable String listenerId) {
		MessageListenerContainer listenerContainer = getListenerContainer(listenerId);

		if (listenerContainer.isRunning()) {
			throw new KafkaConsumerException("Consumer is already running : " + listenerId);
		}

		listenerContainer.start();
	}

	@PutMapping(path = "/{listenerId}/pause")
	public void pause(@PathVariable String listenerId) {
		MessageListenerContainer listenerContainer = getListenerContainer(listenerId);
		if (!listenerContainer.isRunning()) {
			throw new KafkaConsumerException("Consumer is not running: " + listenerId);
		} else if (listenerContainer.isContainerPaused()) {
			throw new KafkaConsumerException("Consumer is already paused: " + listenerId);
		} else if (listenerContainer.isPauseRequested()) {
			throw new KafkaConsumerException("Consumer pause is already requested: " + listenerId);
		}
		listenerContainer.pause();
	}

	@PutMapping(path = "/{listenerId}/resume")
	public void resume(@PathVariable String listenerId) {
		MessageListenerContainer listenerContainer = getListenerContainer(listenerId);
		if (!listenerContainer.isRunning()) {
			throw new KafkaConsumerException("Consumer is not running: " + listenerId);
		} else if (!listenerContainer.isContainerPaused()) {
			throw new KafkaConsumerException("Consumer is not paused: " + listenerId);
		}
		listenerContainer.resume();
	}

	@PutMapping(path = "/{listenerId}/stop")
	public void stop(@PathVariable String listenerId) {
		MessageListenerContainer listenerContainer = getListenerContainer(listenerId);
		if (!listenerContainer.isRunning()) {
			throw new KafkaConsumerException("Consumer is already stopped: " + listenerId);
		}
		listenerContainer.stop();
	}

	@DeleteMapping(path = "{listenerId}")
	public void delete(@PathVariable String listenerId) {
		MessageListenerContainer listenerContainer = getListenerContainer(listenerId);
		listenerContainer.stop();
		kafkaListenerContainerManager.unregisterListener(listenerId);
		Objects.requireNonNull(listenerContainer.getAssignedPartitions())
				.stream()
				.map(TopicPartition::topic)
				.map(KafkaSourceDeletedEvent::new)
				.forEach(eventPublisher::publishEvent);
	}

	private MessageListenerContainer getListenerContainer(String listenerId) {
		Optional<MessageListenerContainer> listenerContainerOpt = kafkaListenerContainerManager.getContainer(listenerId);
		if (listenerContainerOpt.isEmpty()) {
			throw new KafkaConsumerException("No such consumer: " + listenerId);
		}

		return listenerContainerOpt.get();
	}

	private KafkaConsumerResponse createKafkaConsumerResponse(MessageListenerContainer listenerContainer) {
		return KafkaConsumerResponse.builder()
				.groupId(listenerContainer.getGroupId())
				.listenerId(listenerContainer.getListenerId())
				.active(listenerContainer.isRunning())
				.assignments(Optional.ofNullable(listenerContainer.getAssignedPartitions())
						.map(topicPartitions -> topicPartitions.stream()
								.map(this::createKafkaConsumerAssignmentResponse)
								.toList())
						.orElse(null))
				.build();
	}

	private KafkaConsumerAssignment createKafkaConsumerAssignmentResponse(TopicPartition topicPartition) {
		return KafkaConsumerAssignment.builder()
				.topic(topicPartition.topic())
				.partition(topicPartition.partition())
				.build();
	}
}
