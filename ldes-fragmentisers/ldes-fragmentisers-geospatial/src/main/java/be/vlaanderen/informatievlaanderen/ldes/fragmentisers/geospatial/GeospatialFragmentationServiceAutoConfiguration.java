package be.vlaanderen.informatievlaanderen.ldes.fragmentisers.geospatial;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.config.LdesConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentationService;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesmember.repository.LdesMemberRepository;

@Configuration
@ConditionalOnClass(FragmentationService.class)
@EnableConfigurationProperties()
@ComponentScan("be.vlaanderen.informatievlaanderen.ldes")
public class GeospatialFragmentationServiceAutoConfiguration {

	@Bean
	@ConditionalOnMissingBean
	public FragmentationService geospatialFragmentationService(LdesConfig ldesConfig, GeospatialConfig geospatialConfig,
			LdesMemberRepository ldesMemberRepository, LdesFragmentRepository ldesFragmentRepository) {
		return new GeospatialFragmentationService(ldesConfig, ldesMemberRepository, ldesFragmentRepository, new GeospatialFragmentCreator(ldesConfig, geospatialConfig, ldesMemberRepository,
				ldesFragmentRepository));
	}
}
