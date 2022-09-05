package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.queue;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentationExecutor;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentationExecutorImpl;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentationQueueMediator;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@EnableConfigurationProperties()
@ComponentScan("be.vlaanderen.informatievlaanderen.ldes.server")
public class RabbitAutoConfig {

	@Autowired
	FragmentationExecutor fragmentationExecutor;

	@Bean
	public Queue hello() {
		return new Queue("hello");
	}

	@Bean
	public FragmentationQueueMediator rabbitMQSender() {
		return new RabbitMQFragmentationMediator(fragmentationExecutor);
	}

}
