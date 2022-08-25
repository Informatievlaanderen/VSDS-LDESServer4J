package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebased;

import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.RootFragmentService;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.sequential.SequentialFragmentationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.sequential.SequentialFragmentationConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebased.config.TimeBasedFragmentNamingStrategy;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.config.LdesConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.config.LdesFragmentNamingStrategy;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentCreator;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentationService;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesmember.repository.LdesMemberRepository;

@Configuration
// @ConditionalOnProperty(value = "fragmentation.type", havingValue =
// "timebased", matchIfMissing = false)
@ConditionalOnProperty(value = "ldes.fragmentations.timebased.enabled", havingValue = "true")
@EnableConfigurationProperties()
@ComponentScan("be.vlaanderen.informatievlaanderen.ldes.server")
public class TimeBasedFragmentationServiceAutoConfiguration {

	private final Logger logger = LoggerFactory.getLogger(TimeBasedFragmentationServiceAutoConfiguration.class);

	@Bean
	public LdesFragmentNamingStrategy fragmentNamingStrategy() {
		return new TimeBasedFragmentNamingStrategy();
	}

	@Bean
	public FragmentCreator fragmentCreator(LdesConfig ldesConfig,
			SequentialFragmentationConfig sequentialFragmentationConfig,
			LdesFragmentNamingStrategy fragmentNamingStrategy, LdesMemberRepository ldesMemberRepository,
			LdesFragmentRepository ldesFragmentRepository) {
		return new TimeBasedFragmentCreator(ldesConfig, sequentialFragmentationConfig, fragmentNamingStrategy,
				ldesMemberRepository, ldesFragmentRepository);
	}

	@Bean
	public FragmentationService timebased(LdesConfig ldesConfig,
			LdesMemberRepository ldesMemberRepository, LdesFragmentRepository ldesFragmentRepository,
			FragmentCreator fragmentCreator, RootFragmentService rootFragmentService) {
		logger.info("Timebased Fragmentation is configured");
		return new SequentialFragmentationService(ldesConfig, fragmentCreator, ldesMemberRepository,
				ldesFragmentRepository, rootFragmentService);
	}
}
