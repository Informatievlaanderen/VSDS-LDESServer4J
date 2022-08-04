package be.vlaanderen.informatievlaanderen.ldes.fragmentisers.timebased;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.config.LdesConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRespository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentCreator;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesmember.repository.LdesMemberRepository;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass(FragmentCreator.class)
@EnableConfigurationProperties()
@ComponentScan("be.vlaanderen.informatievlaanderen.ldes")
public class TimebasedFragmentiserAutoConfiguration {
	
	@Bean
	@ConditionalOnMissingBean
	public FragmentCreator fragmentCreator(LdesConfig ldesConfig, TimeBasedConfig timeBasedConfig, LdesFragmentRespository ldesFragmentRepository, LdesMemberRepository ldesMemberRepository) {
		return new TimeBasedFragmentCreator(ldesConfig, timeBasedConfig, ldesFragmentRepository, ldesMemberRepository);
	}
}
