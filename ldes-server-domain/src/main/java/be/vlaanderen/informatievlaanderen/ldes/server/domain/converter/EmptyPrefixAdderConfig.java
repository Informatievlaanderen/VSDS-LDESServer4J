package be.vlaanderen.informatievlaanderen.ldes.server.domain.converter;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EmptyPrefixAdderConfig {
	@Bean
	@ConditionalOnMissingBean
	public PrefixAdder emptyPrefixAdder() {
		return model -> model;
	}
}
