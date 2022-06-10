package be.vlaanderen.informatievlaanderen.ldes.server.rest.config;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.rest.converters.LdesFragmentHttpConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;

@Configuration
public class WebConfig {

    @Bean
    public HttpMessageConverter<LdesFragment> ldesFragmentHttpConverter() {
        return new LdesFragmentHttpConverter();
    }
}
