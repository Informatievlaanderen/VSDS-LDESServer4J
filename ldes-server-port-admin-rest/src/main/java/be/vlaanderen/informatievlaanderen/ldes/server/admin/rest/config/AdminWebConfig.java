package be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.config;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.validation.EventStreamValidator;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.validation.LdesConfigShaclValidator;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.validation.ShaclShapeValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AdminWebConfig {

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
