package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.substring;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.substring.SubstringFragmentationStrategy.SUBSTRING_FRAGMENTATION;

@Configuration
@EnableConfigurationProperties()
@ComponentScan("be.vlaanderen.informatievlaanderen.ldes.server")
public class SubstringFragmentationStrategyAutoConfiguration {

	@SuppressWarnings("java:S6830")
	@Bean(SUBSTRING_FRAGMENTATION)
	public SubstringFragmentationStrategyWrapper substringFragmentationStrategyWrapper() {
		return new SubstringFragmentationStrategyWrapper();
	}

}
