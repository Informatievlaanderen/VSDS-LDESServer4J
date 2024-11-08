package be.vlaanderen.informatievlaanderen.ldes.server.ingest.kafka;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.EventStreamDeletedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.KafkaSourceAddedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.MemberIngester;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.kafka.listener.IngestListener;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.validators.IngestValidator;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFLanguages;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerEndpoint;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.config.MethodKafkaListenerEndpoint;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.support.DefaultMessageHandlerMethodFactory;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

@Component
public class KafkaListenerContainerManager {
	private final KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;
	private final KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, String>> kafkaListenerContainerFactory;
	private final KafkaProperties kafkaProperties;
	private final IngestValidator ingestValidator;
	private final MemberIngester memberIngester;

	public KafkaListenerContainerManager(KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry,
	                                     KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, String>> kafkaListenerContainerFactory,
	                                     KafkaProperties kafkaProperties, IngestValidator ingestValidator,
	                                     MemberIngester memberIngester) {
		this.kafkaListenerEndpointRegistry = kafkaListenerEndpointRegistry;
		this.kafkaListenerContainerFactory = kafkaListenerContainerFactory;
		this.kafkaProperties = kafkaProperties;
		this.ingestValidator = ingestValidator;
		this.memberIngester = memberIngester;
	}

	public void registerListener(String listenerId, String collection, String topic, String mimeType) throws NoSuchMethodException {
		kafkaListenerEndpointRegistry.registerListenerContainer(
				createKafkaListenerEndpoint(listenerId, collection, topic, mimeType), kafkaListenerContainerFactory, true
		);
	}

	public Collection<MessageListenerContainer> listContainers() {
		return kafkaListenerEndpointRegistry.getListenerContainers();
	}

	public Optional<MessageListenerContainer> getContainer(String listenerId) {
		return Optional.ofNullable(kafkaListenerEndpointRegistry.getListenerContainer(listenerId));
	}

	public void unregisterListener(String listenerId) {
		kafkaListenerEndpointRegistry.unregisterListenerContainer(listenerId);
	}

	@EventListener(KafkaSourceAddedEvent.class)
	public void onKafkaSourceAdded(KafkaSourceAddedEvent event) throws NoSuchMethodException {
		registerListener(UUID.randomUUID().toString(), event.kafkaSource().collection(), event.kafkaSource().topic(), event.kafkaSource().mimeType());
	}

	@EventListener(EventStreamDeletedEvent.class)
	public void onEventStreamDeleted(EventStreamDeletedEvent event) {
		unregisterListener(event.collectionName());
	}

	private KafkaListenerEndpoint createKafkaListenerEndpoint(String listenerId, String collection, String topic, String mimeType) throws NoSuchMethodException {
		MethodKafkaListenerEndpoint<String, String> kafkaListenerEndpoint = new MethodKafkaListenerEndpoint<>();
		kafkaListenerEndpoint.setId(listenerId);
		kafkaListenerEndpoint.setGroupId(kafkaProperties.getConsumer().getGroupId());
		kafkaListenerEndpoint.setAutoStartup(true);
		kafkaListenerEndpoint.setTopics(topic);
		kafkaListenerEndpoint.setMessageHandlerMethodFactory(new DefaultMessageHandlerMethodFactory());

		Lang lang = RDFLanguages.contentTypeToLang(mimeType);

		kafkaListenerEndpoint.setBean(new IngestListener(collection, lang, ingestValidator, memberIngester));

		kafkaListenerEndpoint.setMethod(IngestListener.class.getMethod("onMessage", ConsumerRecord.class, Acknowledgment.class));
		return kafkaListenerEndpoint;
	}
}
