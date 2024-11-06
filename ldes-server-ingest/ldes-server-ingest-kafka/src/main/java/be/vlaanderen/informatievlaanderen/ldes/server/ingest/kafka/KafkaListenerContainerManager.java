package be.vlaanderen.informatievlaanderen.ldes.server.ingest.kafka;

import be.vlaanderen.informatievlaanderen.ldes.server.ingest.kafka.listener.IngestListener;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFLanguages;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
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

@Component
public class KafkaListenerContainerManager {
	private final KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;
	private final KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, String>> kafkaListenerContainerFactory;
	private final KafkaProperties kafkaProperties;
	private final DefaultListableBeanFactory beanFactory;

	public KafkaListenerContainerManager(KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry, KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, String>> kafkaListenerContainerFactory, KafkaProperties kafkaProperties, DefaultListableBeanFactory beanFactory) {
		this.kafkaListenerEndpointRegistry = kafkaListenerEndpointRegistry;
		this.kafkaListenerContainerFactory = kafkaListenerContainerFactory;
		this.kafkaProperties = kafkaProperties;
		this.beanFactory = beanFactory;
	}

	public KafkaListenerEndpoint createKafkaListenerEndpoint(String listenerId, String topic, String mimeType) throws NoSuchMethodException {
		MethodKafkaListenerEndpoint<String, String> kafkaListenerEndpoint = new MethodKafkaListenerEndpoint<>();
		kafkaListenerEndpoint.setId(listenerId);
		kafkaListenerEndpoint.setGroupId(kafkaProperties.getConsumer().getGroupId());
		kafkaListenerEndpoint.setAutoStartup(true);
		kafkaListenerEndpoint.setTopics(topic);
		kafkaListenerEndpoint.setMessageHandlerMethodFactory(new DefaultMessageHandlerMethodFactory());

		Lang lang = RDFLanguages.contentTypeToLang(mimeType);

		kafkaListenerEndpoint.setBean(new IngestListener(lang));

		kafkaListenerEndpoint.setMethod(IngestListener.class.getMethod("onMessage", ConsumerRecord.class, Acknowledgment.class));
		return kafkaListenerEndpoint;
	}

	public void registerListener(String listenerId, String topic, String mimeType, boolean startImmediately) throws NoSuchMethodException {
		kafkaListenerEndpointRegistry.registerListenerContainer(
				createKafkaListenerEndpoint(listenerId, topic, mimeType), kafkaListenerContainerFactory, startImmediately
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

	private void registerBean(String pipelineName, Object bean) {
		if (!beanFactory.containsSingleton(pipelineName)) {
			beanFactory.registerSingleton(pipelineName, bean);
		}
	}
}
