package be.vlaanderen.informatievlaanderen.ldes.server.rest;

import be.vlaanderen.informatievlaanderen.ldes.server.rest.config.LdesConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.rest.converters.JsonLdConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.rest.services.JsonObjectCreatorImpl;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class TestConfiguration {

    @Bean
    JsonLdConverter jsonLdConverter() {
        LdesConfig ldesConfig = new LdesConfig();
        ldesConfig.setId("test-id");
        ldesConfig.setContext("test-context");
        return new JsonLdConverter(new JsonObjectCreatorImpl(ldesConfig));
    }

}