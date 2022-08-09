package be.vlaanderen.informatievlaanderen.ldes.fragmentisers.timebased;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import be.vlaanderen.informatievlaanderen.ldes.fragmentisers.sequential.SequentialFragmentationConfig;
import be.vlaanderen.informatievlaanderen.ldes.fragmentisers.timebased.config.TimeBasedFragmentNamingStrategy;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.config.LdesConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.config.LdesFragmentNamingStrategy;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentCreator;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentationService;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesmember.repository.LdesMemberRepository;

@Configuration
@ConditionalOnClass(FragmentationService.class)
@EnableConfigurationProperties()
@ComponentScan("be.vlaanderen.informatievlaanderen.ldes")
public class TimeBasedFragmentationServiceAutoConfiguration {

	@Bean
	@ConditionalOnMissingBean(LdesFragmentNamingStrategy.class)
	public LdesFragmentNamingStrategy fragmentNamingStrategy() {
		return new TimeBasedFragmentNamingStrategy();
	}
	
	@Bean
	@ConditionalOnMissingBean(FragmentCreator.class)
	public FragmentCreator fragmentCreator(LdesConfig ldesConfig, SequentialFragmentationConfig sequentialFragmentationConfig,
		LdesFragmentNamingStrategy fragmentNamingStrategy, LdesMemberRepository ldesMemberRepository, LdesFragmentRepository ldesFragmentRepository) {
		return new TimeBasedFragmentCreator(ldesConfig, sequentialFragmentationConfig, fragmentNamingStrategy, ldesMemberRepository, ldesFragmentRepository);
	}
	
	@Bean
	@ConditionalOnMissingBean(FragmentationService.class)
	public FragmentationService timebasedFragmentationService(LdesConfig ldesConfig, SequentialFragmentationConfig sequentialFragmentationConfig,
			LdesFragmentNamingStrategy fragmentNamingStrategy,
			LdesMemberRepository ldesMemberRepository, LdesFragmentRepository ldesFragmentRepository) {
		FragmentCreator fragmentCreator = new TimeBasedFragmentCreator(ldesConfig, sequentialFragmentationConfig, fragmentNamingStrategy, ldesMemberRepository, ldesFragmentRepository);
				
		return new TimeBasedFragmentationService(ldesConfig, fragmentCreator, ldesMemberRepository, ldesFragmentRepository);
	}
}
