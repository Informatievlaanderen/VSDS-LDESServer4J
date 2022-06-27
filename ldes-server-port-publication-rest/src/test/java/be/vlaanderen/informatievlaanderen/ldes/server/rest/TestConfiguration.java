package be.vlaanderen.informatievlaanderen.ldes.server.rest;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.config.ViewConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.rest.converters.LdesFragmentHttpConverter;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class TestConfiguration {

    @Bean
    LdesFragmentHttpConverter jsonLdConverter() {
        return new LdesFragmentHttpConverter();
    }

    @Bean
    ViewConfig viewConfig(){
        ViewConfig viewConfig = new ViewConfig();
        viewConfig.setShape("a");
        viewConfig.setMemberLimit(2L);
        viewConfig.setTimestampPath("b");
        viewConfig.setVersionOfPath("c");
        return viewConfig;
    }

}