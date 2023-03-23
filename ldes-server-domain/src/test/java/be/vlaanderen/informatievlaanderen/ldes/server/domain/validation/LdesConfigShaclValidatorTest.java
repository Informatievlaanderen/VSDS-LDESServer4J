package be.vlaanderen.informatievlaanderen.ldes.server.domain.validation;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.LdesShaclValidationException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.eventstream.valueobjects.EventStream;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesconfig.valueobjects.LdesConfigModel;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.RDFDataMgr;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class LdesConfigShaclValidatorTest {
	private LdesConfigShaclValidator validator;

	private String readShaclShape(String fileName) throws URISyntaxException, IOException {
		ClassLoader classLoader = getClass().getClassLoader();
		URI uri = null;
		uri = Objects.requireNonNull(classLoader.getResource(fileName)).toURI();

		Stream<String> shape = Files.lines(Paths.get(uri));
		return shape.collect(Collectors.joining("\n"));
	}

	private void initializeStreamValidator() {
		validator = new LdesConfigShaclValidator("eventstream/streams/streamShaclShape.ttl");
	}

	@Test
	void when_SupportedClassProvided_thenReturnTrue() {
		initializeStreamValidator();
		assertTrue(validator.supports(LdesConfigModel.class));
	}

	@Test
	void when_UnsupportedClassProvided_thenReturnFalse() {
		initializeStreamValidator();

		assertFalse(validator.supports(EventStream.class));
		assertFalse(validator.supports(Object.class));
	}

	@ParameterizedTest
	@ArgumentsSource(LdesConfigShaclValidatorFileNameProvider.class)
	void when_ValidateProvidedValidEventStream_thenReturnValid(String fileName) throws URISyntaxException {
		initializeStreamValidator();
		final Model validEventStream = readModelFromFile(fileName);

		assertDoesNotThrow(() -> validator.validateShape(validEventStream));
	}

	@Test
	void when_ValidateValidShaclShape_thenReturnValid() throws URISyntaxException {
		validator = new LdesConfigShaclValidator("eventstream/streams/shapeShaclShape.ttl");

		final Model validShaclShape = readModelFromFile("valid-shacl-shape.ttl");

		assertDoesNotThrow(() -> validator.validateShape(validShaclShape));
	}

	@Test
	void when_validateInvalidShaclShape_thenReturnInvalid() throws URISyntaxException {
		validator = new LdesConfigShaclValidator("eventstream/streams/shapeShaclShape.ttl");

		final Model model = readModelFromFile("example-shape.ttl");
		assertThrows(LdesShaclValidationException.class, () -> validator.validateShape(model));
	}

	@Test
	void when_ValidateProvidedInvalidEventStream_thenReturnInvalid() throws URISyntaxException {
		initializeStreamValidator();

		final Model model = readModelFromFile("ldes-1.ttl");
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
