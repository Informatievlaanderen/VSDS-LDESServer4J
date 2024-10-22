package be.vlaanderen.informatievlaanderen.ldes.server.ingest.validation.defaultimpl;

import be.vlaanderen.informatievlaanderen.ldes.server.ingest.validation.MemberIngestValidator;
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
	public MemberIngestValidator ingestValidatorCollection(ModelIngestValidatorFactory modelIngestValidatorFactory) {
		log.info("Using default validator for ldes-server-port-ingest");
		return new MemberIngestValidatorImpl(modelIngestValidatorFactory);
	}

	@ConditionalOnMissingBean
	@Bean
	public ModelIngestValidatorFactory modelIngestValidatorFactory() {
		return new ModelIngestValidatorFactory();
	}

}
