package be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.validation;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.LdesShaclValidationException;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.RDFDataMgr;
import org.junit.jupiter.api.Test;

import java.net.URISyntaxException;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

class ShaclShapeValidatorTest {
	private final ShaclShapeValidator validator = new ShaclShapeValidator();

	@Test
	void test_classSupport() {
		assertTrue(validator.supports(Model.class));
	}

	@Test
	void when_ValidateValidShaclShape_thenReturnValid() throws URISyntaxException {
		final Model validShaclShape = readModelFromFile("eventstream/streams/valid-shape.ttl");

		assertDoesNotThrow(() -> validator.validateShape(validShaclShape));
	}

	@Test
	void when_validateInvalidShaclShape_thenReturnInvalid() throws URISyntaxException {
		final Model model = readModelFromFile("eventstream/streams/invalid-shape.ttl");

		assertThrows(LdesShaclValidationException.class, () -> validator.validateShape(model));
	}

	private Model readModelFromFile(String fileName) throws URISyntaxException {
		ClassLoader classLoader = getClass().getClassLoader();
		String uri = Objects.requireNonNull(classLoader.getResource(fileName)).toURI().toString();
		return RDFDataMgr.loadModel(uri);
	}

}
