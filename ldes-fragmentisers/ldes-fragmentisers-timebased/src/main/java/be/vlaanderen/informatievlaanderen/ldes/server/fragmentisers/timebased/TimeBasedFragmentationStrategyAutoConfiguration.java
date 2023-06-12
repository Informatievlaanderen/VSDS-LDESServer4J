package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebased;

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

	@Bean("TimebasedFragmentation")
	public TimebasedFragmentationStrategyWrapper timebasedFragmentationStrategyWrapper() {
		LOGGER.warn("Using deprecated timebased fragmentation. For more information, refer to " +
				"https://github.com/Informatievlaanderen/VSDS-LDESServer4J/blob/main/ldes-fragmentisers/ldes-fragmentisers-timebased/README.MD");
		return new TimebasedFragmentationStrategyWrapper();
	}
}
