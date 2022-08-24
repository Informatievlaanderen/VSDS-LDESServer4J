package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.hierarchy;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.config.LdesConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentCreator;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.hierarchy.fragments.GeospatialFragmentCreator;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties()
@ComponentScan("be.vlaanderen.informatievlaanderen.ldes.server")
public class HierarchyGeospatialFragmentationServiceAutoConfiguration {

	@Bean("hierarchy-geospatial")
	public HierarchyGeospatialFragmentationUpdater geospatialFragmentationService() {
		return new HierarchyGeospatialFragmentationUpdater();
	}



}

