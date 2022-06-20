package be.vlaanderen.informatievlaanderen.ldes.server.rest;

import be.vlaanderen.informatievlaanderen.ldes.server.rest.converters.LdesFragmentHttpConverter;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class TestConfiguration {

    @Bean
    LdesFragmentHttpConverter jsonLdConverter() {
        return new LdesFragmentHttpConverter();
    }

}