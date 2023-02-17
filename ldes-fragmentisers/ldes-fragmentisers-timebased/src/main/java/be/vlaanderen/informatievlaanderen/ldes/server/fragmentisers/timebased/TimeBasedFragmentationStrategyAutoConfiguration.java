package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebased;

import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebased.services.TimeBasedFragmentCreator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties()
@ComponentScan("be.vlaanderen.informatievlaanderen.ldes.server")
public class TimeBasedFragmentationStrategyAutoConfiguration {

	private static final Logger LOGGER = LoggerFactory.getLogger(TimeBasedFragmentationStrategyAutoConfiguration.class);

	@Bean("timebased")
	public TimebasedFragmentationStrategyWrapper timebasedFragmentationStrategyWrapper() {
		LOGGER.warn("You are still using timebased fragmentation. This is deprecated and no longer supported. ");
		return new TimebasedFragmentationStrategyWrapper();
	}
}
