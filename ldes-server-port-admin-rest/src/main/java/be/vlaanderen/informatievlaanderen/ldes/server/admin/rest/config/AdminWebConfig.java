package be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.config;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.converters.LdesStreamModelConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.validation.LdesStreamShaclValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AdminWebConfig {
	@Bean
	public LdesStreamModelConverter ldesStreamModelConverter() {
		return new LdesStreamModelConverter();
	}

	@Bean
	public LdesStreamShaclValidator ldesStreamShaclValidator() {
		// shape needs to be retrieved from the config, needs to be the shacl shape of
		// the stream
		final String SHAPE = "";
		return new LdesStreamShaclValidator(SHAPE);
	}
}
