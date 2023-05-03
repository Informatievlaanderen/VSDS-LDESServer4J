package be.vlaanderen.informatievlaanderen.ldes.server.ingest.config;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.shacl.ShaclCollection;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.AppConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.validation.DefaultMemberIngestValidator;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.validation.MemberIngestValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class IngestValidationConfig {

	private static final Logger log = LoggerFactory.getLogger(IngestValidationConfig.class);

	@ConditionalOnMissingBean
	@Bean
	public MemberIngestValidator defaultValidator(AppConfig appConfig, ShaclCollection shaclCollection) {
		log.info("Using default validator for ldes-server-port-ingest");
		return new DefaultMemberIngestValidator(appConfig, shaclCollection);
	}

}
