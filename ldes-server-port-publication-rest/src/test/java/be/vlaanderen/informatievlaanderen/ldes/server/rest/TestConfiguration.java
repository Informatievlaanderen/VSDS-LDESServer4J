package be.vlaanderen.informatievlaanderen.ldes.server.rest;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.LdesFragmentConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.repositories.LdesMemberRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.rest.converters.LdesFragmentHttpConverter;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class TestConfiguration {

    @MockBean
    private LdesMemberRepository ldesMemberRepository;

    @Bean
    LdesFragmentHttpConverter jsonLdConverter() {
        return new LdesFragmentHttpConverter(new LdesFragmentConverter(ldesMemberRepository));
    }

}