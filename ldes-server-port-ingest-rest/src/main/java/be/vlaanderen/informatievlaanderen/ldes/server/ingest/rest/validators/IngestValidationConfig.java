package be.vlaanderen.informatievlaanderen.ldes.server.ingest.rest.validators;

import be.vlaanderen.informatievlaanderen.ldes.server.ingest.rest.validators.nodesingestvalidator.NodesIngestValidator;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.rest.validators.pathsingestvalidator.PathsIngestValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

@Configuration
public class IngestValidationConfig {

    private static final Logger log = LoggerFactory.getLogger(IngestValidationConfig.class);
    @Order(1)
    @Bean
    public IngestValidator ingestValidatorNodes() {
        log.info("Using nodes validator for ldes-server-port-ingest");
        return new NodesIngestValidator();
    }

    @Order(2)
    @Bean
    public IngestValidator ingestValidatorPaths() {
        log.info("Using paths validator for ldes-server-port-ingest");
        return new PathsIngestValidator();
    }
}
