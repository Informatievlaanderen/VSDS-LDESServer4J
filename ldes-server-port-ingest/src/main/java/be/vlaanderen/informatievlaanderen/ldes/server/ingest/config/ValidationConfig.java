package be.vlaanderen.informatievlaanderen.ldes.server.ingest.config;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.AppConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.validation.DefaultMemberIngestValidator;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.validation.MemberIngestValidator;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.validation.ModelValidatorCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ValidationConfig {

	private static final Logger log = LoggerFactory.getLogger(ValidationConfig.class);

	@ConditionalOnMissingBean
	@Bean
	public MemberIngestValidator ingestValidatorCollection(ModelValidatorCollection modelValidatorCollection) {
		log.info("Using default validator for ldes-server-port-ingest");
		return new DefaultMemberIngestValidator(modelValidatorCollection);
	}

	@ConditionalOnMissingBean
	@Bean
	public ModelValidatorCollection ingestValidatorCollection(AppConfig appConfig) {
		log.info("Using default validator for ldes-server-port-ingest");
		return new ModelValidatorCollection(appConfig);
	}

}
