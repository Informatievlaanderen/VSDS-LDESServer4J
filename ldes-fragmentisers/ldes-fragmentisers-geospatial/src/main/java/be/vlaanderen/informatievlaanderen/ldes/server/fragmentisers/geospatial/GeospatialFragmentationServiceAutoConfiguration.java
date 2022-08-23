package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.config.LdesConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.config.LdesFragmentNamingStrategy;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentCreator;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentationService;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesmember.repository.LdesMemberRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.bucketising.GeospatialBucketiser;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.fragments.GeospatialFragmentCreator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(value = "fragmentation.type", havingValue = "geospatial")
@EnableConfigurationProperties()
@ComponentScan("be.vlaanderen.informatievlaanderen.ldes.server")
public class GeospatialFragmentationServiceAutoConfiguration {

	private final Logger logger = LoggerFactory.getLogger(GeospatialFragmentationServiceAutoConfiguration.class);

	@Bean
	public FragmentationService geospatialFragmentationService(LdesConfig ldesConfig,
			LdesMemberRepository ldesMemberRepository,
			LdesFragmentRepository ldesFragmentRepository,
			GeospatialBucketiser geospatialBucketiser,
			LdesFragmentNamingStrategy ldesFragmentNamingStrategy) {
		logger.info("Geospatial Fragmentation is configured");
		return new GeospatialFragmentationService(ldesConfig, ldesMemberRepository, ldesFragmentRepository,
				new GeospatialFragmentCreator(ldesConfig, ldesFragmentNamingStrategy), geospatialBucketiser
		);
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
