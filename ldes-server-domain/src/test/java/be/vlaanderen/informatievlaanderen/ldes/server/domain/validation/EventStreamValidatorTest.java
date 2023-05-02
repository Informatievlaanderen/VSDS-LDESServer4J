package be.vlaanderen.informatievlaanderen.ldes.server.domain.validation;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.valueobjects.EventStream;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.LdesShaclValidationException;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.RDFDataMgr;
import org.junit.jupiter.api.Test;

import java.net.URISyntaxException;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

class EventStreamValidatorTest {

	private final EventStreamValidator validator = new EventStreamValidator();

	@Test
	void test_support() {
		Model model = ModelFactory.createDefaultModel();
		assertTrue(validator.supports(model.getClass()));
		assertTrue(validator.supports(Model.class));
		assertFalse(validator.supports(EventStream.class));
		assertFalse(validator.supports(Integer.class));
	}

	@Test
	void when_validLdesProvided_then_returnValid() throws URISyntaxException {
		Model model = readModelFromFile("eventstream/streams/valid-ldes.ttl");
		assertDoesNotThrow(() -> validator.validateShape(model));
	}

	@Test
	void when_invalidLdesProvided_then_returnInvalid() throws URISyntaxException {
		Model model = readModelFromFile("eventstream/streams/invalid-shape.ttl");
		assertThrows(LdesShaclValidationException.class, () -> validator.validateShape(model));
	}

	private Model readModelFromFile(String fileName) throws URISyntaxException {
		ClassLoader classLoader = getClass().getClassLoader();
		String uri = Objects.requireNonNull(classLoader.getResource(fileName)).toURI().toString();
		return RDFDataMgr.loadModel(uri);
	}
}