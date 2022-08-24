package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebased;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentationServiceImpl;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.sequential.SequentialFragmentationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.sequential.SequentialFragmentationConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.config.LdesConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentCreator;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentationService;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesmember.repository.LdesMemberRepository;

@Configuration
@ConditionalOnProperty(value = "fragmentation.type", havingValue = "timebased", matchIfMissing = true)
@EnableConfigurationProperties()
@ComponentScan("be.vlaanderen.informatievlaanderen.ldes.server")
public class TimeBasedFragmentationServiceAutoConfiguration {

	private final Logger logger = LoggerFactory.getLogger(TimeBasedFragmentationServiceAutoConfiguration.class);

	@Bean
	public FragmentCreator fragmentCreator(LdesConfig ldesConfig,
			SequentialFragmentationConfig sequentialFragmentationConfig,
										   LdesMemberRepository ldesMemberRepository,
			LdesFragmentRepository ldesFragmentRepository) {
		return new TimeBasedFragmentCreator(ldesConfig, sequentialFragmentationConfig,
				ldesMemberRepository, ldesFragmentRepository);
	}

	@Bean
	public FragmentationService sequentialFragmentationService(LdesConfig ldesConfig,
			LdesMemberRepository ldesMemberRepository, LdesFragmentRepository ldesFragmentRepository,
			FragmentCreator fragmentCreator) {
		logger.info("Timebased Fragmentation is configured");
		return new SequentialFragmentationService(new FragmentationServiceImpl(ldesFragmentRepository, ldesMemberRepository, ldesConfig),ldesConfig, fragmentCreator, ldesMemberRepository,
				ldesFragmentRepository);
	}
}
