package be.vlaanderen.informatievlaanderen.ldes.server.ingestion.rest;

import be.vlaanderen.informatievlaanderen.ldes.server.ingestion.rest.converters.LdesMemberNquadsConverter;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class TestConfiguration {
    @Bean
    public LdesMemberNquadsConverter nquadsConverter() {
        return new LdesMemberNquadsConverter();
    }
}