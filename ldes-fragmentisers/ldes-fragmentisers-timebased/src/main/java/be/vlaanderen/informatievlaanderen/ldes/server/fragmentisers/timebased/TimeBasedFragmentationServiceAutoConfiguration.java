package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebased;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties()
@ComponentScan("be.vlaanderen.informatievlaanderen.ldes.server")
public class TimeBasedFragmentationServiceAutoConfiguration {

	@Bean("timebased")
	public TimebasedFragmentationUpdater geospatialFragmentationService() {
		return new TimebasedFragmentationUpdater();
	}
}
