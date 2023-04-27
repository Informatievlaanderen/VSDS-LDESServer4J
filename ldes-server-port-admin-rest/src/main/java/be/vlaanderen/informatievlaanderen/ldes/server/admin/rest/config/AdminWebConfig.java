package be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.config;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.converters.LdesConfigModelConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.converters.LdesConfigModelListConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.converters.ModelConverter;
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
	public LdesConfigModelListConverter ldesConfigModelListConverter() {
		return new LdesConfigModelListConverter();
	}

	@Bean
	public ModelConverter modelConverter() {
		return new ModelConverter();
	}

	@Bean("configShaclValidator")
	public LdesConfigShaclValidator ldesConfigShaclValidator() {
		return new LdesConfigShaclValidator("configShaclShape.ttl");
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
}
