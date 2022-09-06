package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.mediator;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentationExecutor;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentationMediator;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.queue.FragmenterConstants.DEFAULT_LDES_MEMBER_FRAGMENTATION_QUEUE;

@Configuration
@EnableConfigurationProperties()
@ConditionalOnProperty(name = "ldes.queue", havingValue = "rabbit-mq")
@EnableRabbit
@ComponentScan("be.vlaanderen.informatievlaanderen.ldes.server")
public class RabbitFragmentationMediatorAutoConfig {

	@Autowired
	FragmentationExecutor fragmentationExecutor;

	@Bean
	public Queue defaultLdesMemberFragmentationQueue() {
		return new Queue(DEFAULT_LDES_MEMBER_FRAGMENTATION_QUEUE);
	}

	@Bean
	public FragmentationMediator rabbitMQFragmentationMediator() {
		return new RabbitMQFragmentationMediator(fragmentationExecutor);
	}

}
