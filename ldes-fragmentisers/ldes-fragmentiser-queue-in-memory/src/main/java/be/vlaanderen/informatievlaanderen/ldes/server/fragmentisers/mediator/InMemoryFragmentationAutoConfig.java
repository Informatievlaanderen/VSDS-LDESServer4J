package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.mediator;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentationExecutor;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentationMediator;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties()
@ConditionalOnProperty(name = "ldes.queue", havingValue = "in-memory", matchIfMissing = true)
@ComponentScan("be.vlaanderen.informatievlaanderen.ldes.server")
public class InMemoryFragmentationAutoConfig {
	@Autowired
	FragmentationExecutor fragmentationExecutor;

	@Autowired
	MeterRegistry meterRegistry;

	@Bean
	public FragmentationMediator inMemoryFragmentationMediator() {
		return new InMemoryFragmentationMediator(fragmentationExecutor, meterRegistry);
	}
}
