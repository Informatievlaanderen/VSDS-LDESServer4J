package be.vlaanderen.informatievlaanderen.ldes.server.domain.validation;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.LdesShaclValidationException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.eventstream.valueobjects.EventStream;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesconfig.valueobjects.LdesConfigModel;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.RDFDataMgr;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.net.URISyntaxException;
import java.util.Objects;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class LdesConfigShaclValidatorTest {
	private LdesConfigShaclValidator validator;

	@BeforeEach
	void setUp() {
		validator = new LdesConfigShaclValidator("eventstream/streams/shaclShapes/streamShaclShape.ttl");
	}

	@Test
	void when_SupportedClassProvided_thenReturnTrue() {
		assertTrue(validator.supports(LdesConfigModel.class));
	}

	@Test
	void when_UnsupportedClassProvided_thenReturnFalse() {
		assertFalse(validator.supports(EventStream.class));
		assertFalse(validator.supports(Object.class));
	}

	@ParameterizedTest
	@ArgumentsSource(LdesConfigShaclValidatorFileNameProvider.class)
	void when_ValidateProvidedValidEventStream_thenReturnValid(String fileName) throws URISyntaxException {
		final Model validEventStream = readModelFromFile(fileName);

		assertDoesNotThrow(() -> validator.validateShape(validEventStream));
	}

	@Test
	void when_ValidateProvidedInvalidEventStream_thenReturnInvalid() throws URISyntaxException {
		final Model model = readModelFromFile("ldes-empty.ttl");
		assertThrows(LdesShaclValidationException.class, () -> validator.validateShape(model));
	}

	private Model readModelFromFile(String fileName) throws URISyntaxException {
		ClassLoader classLoader = getClass().getClassLoader();
		String uri = Objects.requireNonNull(classLoader.getResource("eventstream/streams/" + fileName)).toURI()
				.toString();
		return RDFDataMgr.loadModel(uri);
	}

	static class LdesConfigShaclValidatorFileNameProvider implements ArgumentsProvider {

		@Override
		public Stream<Arguments> provideArguments(ExtensionContext extensionContext) throws Exception {
			return Stream.of(Arguments.of("valid-ldes.ttl")
			//// Below is commented, because eventstreams without shape are not valid yet,
			//// but are supposed to in the future
			// ,Arguments.of("valid-ldes-without-shape.ttl")

			//// Below is commented, because eventsteams with a view are not valid yet,
			//// buy are supposed to in the future
			// ,Arguments.of("valid-ldes-with-view.ttl")
			);
		}
	}
}
