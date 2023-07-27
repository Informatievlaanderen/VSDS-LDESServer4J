package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebasedhierarchical;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebasedhierarchical.TimeBasedFragmentationStrategy.TIMEBASED_FRAGMENTATION_HIERARCHICAL;

@Configuration
@EnableConfigurationProperties()
@ComponentScan("be.vlaanderen.informatievlaanderen.ldes.server")
public class TimeBasedFragmentationStrategyAutoConfiguration {

	@Bean(TIMEBASED_FRAGMENTATION_HIERARCHICAL)
	public TimeBasedFragmentationStrategyWrapper timeBasedFragmentationStrategyWrapper() {
		return new TimeBasedFragmentationStrategyWrapper();
	}

}