package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties()
@ComponentScan("be.vlaanderen.informatievlaanderen.ldes.server")
public class GeospatialFragmentationServiceAutoConfiguration {

	@Bean("geospatial")
	public GeospatialFragmentationUpdater geospatialFragmentationService() {
		return new GeospatialFragmentationUpdater();
	}

}
