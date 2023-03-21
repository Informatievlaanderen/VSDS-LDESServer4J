package be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.config;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.converters.LdesConfigModelConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.validation.LdesStreamShaclValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AdminWebConfig {
	@Bean
	public LdesConfigModelConverter ldesStreamModelConverter() {
		return new LdesConfigModelConverter();
	}

	@Bean
	public LdesStreamShaclValidator ldesStreamShaclValidator() {
		// shape needs to be retrieved from the config, needs to be the shacl shape of
		// the stream
		final String SHAPE = "";
		return new LdesStreamShaclValidator(SHAPE);
	}
}
