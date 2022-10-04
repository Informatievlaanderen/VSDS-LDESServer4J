package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.substring;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties()
@ComponentScan("be.vlaanderen.informatievlaanderen.ldes.server")
public class SubstringFragmentationStrategyAutoConfiguration {

	@Bean("substring")
	public SubstringFragmentationStrategyWrapper substringFragmentationStrategyWrapper() {
		return new SubstringFragmentationStrategyWrapper();
	}

}
