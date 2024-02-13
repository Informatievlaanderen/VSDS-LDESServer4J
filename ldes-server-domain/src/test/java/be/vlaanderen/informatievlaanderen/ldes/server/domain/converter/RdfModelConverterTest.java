package be.vlaanderen.informatievlaanderen.ldes.server.domain.converter;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.RelativeUrlException;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.stream.Stream;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;

import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@WireMockTest(httpPort = 10101)
class RdfModelConverterTest {

    private final RdfModelConverter rdfModelConverter = new RdfModelConverter();

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(rdfModelConverter, "useRelativeUrl", true);
        ReflectionTestUtils.setField(rdfModelConverter, "maxJsonLdCacheCapacity", 5);
    }

    @Test
    void when_ContextIsUsedForParsingMultipleModels_JsonLdContextIsOnlyFetchedOnceFromExternalSource() {
        for (int i = 0; i < 5; i++) {
            RDFParser
                    .source("jsonld-with-context.jsonld")
                    .context(rdfModelConverter.getContext())
                    .toModel();
        }

        // verify that with context, caching is present
        WireMock.verify(1, getRequestedFor(urlEqualTo("/context.jsonld")));

        for (int i = 0; i < 5; i++) {
            RDFParser
                    .source("jsonld-with-context.jsonld")
                    .toModel();
        }

        // verify that without context, no caching is present
        WireMock.verify(6, getRequestedFor(urlEqualTo("/context.jsonld")));
    }

    @ParameterizedTest
    @ArgumentsSource(ContentTypeArgumentsProvider.class)
    void when_UseRelativeUrlAndIncompatibleContentType_Then_ThrowException(Lang type) {
        assertThatThrownBy(()->rdfModelConverter.checkLangForRelativeUrl(type)).isInstanceOf(RelativeUrlException.class);
    }

    static class ContentTypeArgumentsProvider implements ArgumentsProvider {
        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) {
            return Stream.of(
                    Arguments.of(Lang.NQUADS),
                    Arguments.of(Lang.NTRIPLES),
                    Arguments.of(Lang.RDFJSON),
                    Arguments.of(Lang.RDFXML));
        }
    }
}
