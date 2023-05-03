package be.vlaanderen.informatievlaanderen.ldes.server.ingest.config;

import be.vlaanderen.informatievlaanderen.ldes.server.ingest.validation.MemberIngestValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class IngestConfig {

	private static final Logger log = LoggerFactory.getLogger(IngestConfig.class);

	@ConditionalOnMissingBean
	@Bean
	public MemberIngestValidator defaultValidator() {
		log.warn("No validator configured for ldes-server-port-ingest");
		return member -> {
		};
	}

}
