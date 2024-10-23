package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.reference;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.reference.ReferenceFragmentationStrategy.REFERENCE_FRAGMENTATION;

@Configuration
@EnableConfigurationProperties()
@ComponentScan("be.vlaanderen.informatievlaanderen.ldes.server")
public class ReferenceFragmentationStrategyAutoConfiguration {

	@SuppressWarnings("java:S6830")
	@Bean(REFERENCE_FRAGMENTATION)
	public ReferenceFragmentationStrategyWrapper geospatialFragmentationStrategyWrapper() {
		return new ReferenceFragmentationStrategyWrapper();
	}

}
