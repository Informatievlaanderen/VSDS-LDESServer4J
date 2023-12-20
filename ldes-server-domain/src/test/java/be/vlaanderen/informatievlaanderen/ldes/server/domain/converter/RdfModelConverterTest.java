package be.vlaanderen.informatievlaanderen.ldes.server.domain.converter;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.RelativeUrlException;
import org.apache.jena.riot.Lang;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class RdfModelConverterTest {

    private final RdfModelConverter rdfModelConverter = new RdfModelConverter();

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(rdfModelConverter, "useRelativeUrl", true);
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
