package be.vlaanderen.informatievlaanderen.ldes.fragmentisers;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import be.vlaanderen.informatievlaanderen.ldes.fragmentisers.services.FragmentFetchServiceImpl;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.config.LdesConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.config.LdesFragmentNamingStrategy;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentFetchService;

@Configuration
@EnableConfigurationProperties()
@ComponentScan("be.vlaanderen.informatievlaanderen.ldes")
public class FragmentiserAutoConfiguration {


	@Bean
	@ConditionalOnMissingBean(FragmentFetchService.class)
	public FragmentFetchService fragmentFetchService(LdesConfig ldesConfig, LdesFragmentNamingStrategy ldesFragmentNamingStrategy, LdesFragmentRepository ldesFragmentRepository) {
		return new FragmentFetchServiceImpl(ldesConfig, ldesFragmentNamingStrategy, ldesFragmentRepository);
	}
}
