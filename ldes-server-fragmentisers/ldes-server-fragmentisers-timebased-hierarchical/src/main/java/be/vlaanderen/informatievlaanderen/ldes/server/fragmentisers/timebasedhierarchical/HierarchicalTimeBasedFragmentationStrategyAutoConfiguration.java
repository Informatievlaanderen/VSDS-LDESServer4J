package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebasedhierarchical;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebasedhierarchical.HierarchicalTimeBasedFragmentationStrategy.TIMEBASED_FRAGMENTATION_HIERARCHICAL;

@Configuration
@EnableConfigurationProperties()
@ComponentScan("be.vlaanderen.informatievlaanderen.ldes.server")
public class HierarchicalTimeBasedFragmentationStrategyAutoConfiguration {

	@SuppressWarnings("java:S6830")
	@Bean(TIMEBASED_FRAGMENTATION_HIERARCHICAL)
	public HierarchicalTimeBasedFragmentationStrategyWrapper timeBasedFragmentationStrategyWrapper() {
		return new HierarchicalTimeBasedFragmentationStrategyWrapper();
	}

}
