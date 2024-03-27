package be.vlaanderen.informatievlaanderen.ldes.server.ingest.rest.validators.nodesingestvalidator;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.ShaclValidationException;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.RDFDataMgr;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.net.URISyntaxException;
import java.util.Objects;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class NodesIngestValidatorTest {
    private NodesIngestValidator validator;

    @BeforeEach
    void setup() {
        validator = new NodesIngestValidator();
    }

    @ParameterizedTest
    @ArgumentsSource(IncorrectMemberArgumentsProvider.class)
    void when_IncorrectMemberReceived_Then_ValidationThrowsException(Model model) {
        assertThrows(ShaclValidationException.class, () -> validator.validate(model, "collection"));
    }

    @ParameterizedTest
    @ArgumentsSource(CorrectMemberArgumentsProvider.class)
    void when_CorrectMemberReceived_Then_ValidationDoesNotThrowException(Model model) {
        assertDoesNotThrow(() -> validator.validate(model, "collection"));
    }

    static class IncorrectMemberArgumentsProvider implements ArgumentsProvider {
        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) throws Exception {
            return Stream.of(
                    Arguments.of(readModelFromFile("example-ldes-member-dangling-nodes.nq")),
                    Arguments.of(readModelFromFile("example-ldes-member-blank-node.nq")));
        }
    }

    static class CorrectMemberArgumentsProvider implements ArgumentsProvider {
        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) throws Exception {
            return Stream.of(
                    Arguments.of(readModelFromFile("example-ldes-member-state.nq")),
                    Arguments.of(readModelFromFile("example-ldes-member.nq")));
        }
    }

    private static Model readModelFromFile(String fileName) throws URISyntaxException {
        return RDFDataMgr.loadModel(fileName);
    }

}