package be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.config;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.converters.LdesConfigModelConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.validation.LdesConfigShaclValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AdminWebConfig {
    @Bean
    public LdesConfigModelConverter ldesStreamModelConverter() {
        return new LdesConfigModelConverter();
    }
    @Bean("streamShaclValidator")
    public LdesConfigShaclValidator ldesStreamShaclValidator() {
        return new LdesConfigShaclValidator("streamShaclShape.ttl");
    }

	@Bean(name = "viewShaclValidator")
	public LdesConfigShaclValidator ldesViewShaclValidator() {
		// shaclShape for view still needs to be added
		return new LdesConfigShaclValidator("viewShaclShape.ttl");
	}

	@Bean(name = "shapeShaclValidator")
	public LdesConfigShaclValidator ldesShapeShaclValidator() {
		return new LdesConfigShaclValidator("shapeShaclShape.ttl");
	}
}
