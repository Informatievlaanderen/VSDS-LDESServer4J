package be.vlaanderen.informatievlaanderen.ldes.server.fragmenters.mediator;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentationExecutor;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentationMediator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties()
@ConditionalOnProperty(name = "ldes.queue", havingValue = "none", matchIfMissing = true)
@ComponentScan("be.vlaanderen.informatievlaanderen.ldes.server")
public class DirectFragmentationMediatorAutoConfig {
	@Bean
	public FragmentationMediator defaultFragmentationMediator(FragmentationExecutor fragmentationExecutor) {
		return new DirectFragmentationMediator(fragmentationExecutor);
	}
}
