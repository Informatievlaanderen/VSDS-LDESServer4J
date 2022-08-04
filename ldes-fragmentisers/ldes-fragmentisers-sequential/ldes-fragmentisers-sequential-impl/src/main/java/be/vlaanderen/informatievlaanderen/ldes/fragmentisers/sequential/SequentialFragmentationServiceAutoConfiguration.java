package be.vlaanderen.informatievlaanderen.ldes.fragmentisers.sequential;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
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

@Configuration
@ConditionalOnClass(FragmentationService.class)
@EnableConfigurationProperties()
@ComponentScan("be.vlaanderen.informatievlaanderen.ldes")
public class SequentialFragmentationServiceAutoConfiguration {

	@Bean
	@ConditionalOnMissingBean
	public FragmentationService sequentialFragmentationService(LdesConfig ldesConfig, SequentialFragmentationConfig timeBasedConfig, LdesFragmentNamingStrategy ldesFragmentNamingStrategy,
			LdesMemberRepository ldesMemberRepository, LdesFragmentRepository ldesFragmentRepository) {
		FragmentCreator fragmentCreator = new SequentialFragmentCreatorImpl(ldesConfig, timeBasedConfig, ldesFragmentNamingStrategy, ldesMemberRepository, ldesFragmentRepository);
				
		return new SequentialFragmentationService(ldesConfig, fragmentCreator, ldesMemberRepository, ldesFragmentRepository);
	}
}
