package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial;

import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.RootFragmentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.config.LdesConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.config.LdesFragmentNamingStrategy;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentCreator;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentationService;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesmember.repository.LdesMemberRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.bucketising.GeospatialBucketiser;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.fragments.GeospatialFragmentCreator;

@Configuration
@ConditionalOnProperty(value = "ldes.fragmentations.geospatial.enabled", havingValue = "true")
@EnableConfigurationProperties()
@ComponentScan("be.vlaanderen.informatievlaanderen.ldes.server")
public class GeospatialFragmentationServiceAutoConfiguration {

	private final Logger logger = LoggerFactory.getLogger(GeospatialFragmentationServiceAutoConfiguration.class);

	@Bean
	public FragmentationService geospatial(LdesConfig ldesConfig,
			LdesMemberRepository ldesMemberRepository,
			LdesFragmentRepository ldesFragmentRepository,
			GeospatialBucketiser geospatialBucketiser,
			LdesFragmentNamingStrategy ldesFragmentNamingStrategy,
			RootFragmentService rootFragmentService) {
		logger.info("Geospatial Fragmentation is configured");
		return new GeospatialFragmentationService(ldesConfig, ldesMemberRepository, ldesFragmentRepository,
				new GeospatialFragmentCreator(ldesConfig, ldesFragmentNamingStrategy), geospatialBucketiser,
				rootFragmentService);
	}

	@Bean
	public FragmentCreator fragmentCreator(LdesConfig ldesConfig,
			LdesFragmentNamingStrategy fragmentNamingStrategy) {
		return new GeospatialFragmentCreator(ldesConfig, fragmentNamingStrategy);
	}

	@Bean
	public LdesFragmentNamingStrategy fragmentNamingStrategy() {
		return new GeospatialFragmentNamingStrategy();
	}

}
