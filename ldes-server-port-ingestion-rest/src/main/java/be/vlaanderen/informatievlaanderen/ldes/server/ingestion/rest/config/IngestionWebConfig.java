package be.vlaanderen.informatievlaanderen.ldes.server.ingestion.rest.config;

import be.vlaanderen.informatievlaanderen.ldes.server.ingestion.rest.converters.LdesMemberNquadsConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class IngestionWebConfig {
    @Bean
    public LdesMemberNquadsConverter nquadsConverter() {
        return new LdesMemberNquadsConverter();
    }

}
