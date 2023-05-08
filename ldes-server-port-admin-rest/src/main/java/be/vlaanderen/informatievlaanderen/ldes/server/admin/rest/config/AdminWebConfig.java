package be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.config;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.converters.EventStreamHttpConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.converters.EventStreamListHttpConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.converters.LdesConfigModelConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.validation.EventStreamValidator;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.validation.LdesConfigShaclValidator;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.validation.ShaclShapeValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AdminWebConfig {
	@Bean
	public LdesConfigModelConverter ldesConfigModelConverter() {
		return new LdesConfigModelConverter();
	}

	@Bean
	public EventStreamListHttpConverter eventStreamListHttpConverter() {
		return new EventStreamListHttpConverter();
	}

	@Bean
	public EventStreamHttpConverter eventStreamHttpConverter() {
		return new EventStreamHttpConverter();
	}

	@Bean(name = "viewShaclValidator")
	public LdesConfigShaclValidator ldesViewShaclValidator() {
		// shaclShape for view still needs to be added
		return new LdesConfigShaclValidator("viewShaclShape.ttl");
	}

	@Bean
	public ShaclShapeValidator shaclShapeValidator() {
		return new ShaclShapeValidator();
	}

	@Bean
	public EventStreamValidator eventStreamValidator() {
		return new EventStreamValidator();
	}
}
