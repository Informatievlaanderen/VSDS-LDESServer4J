package be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.validation;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.spi.LdesShaclValidationException;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.RDFDataMgr;
import org.junit.jupiter.api.Test;

import java.net.URISyntaxException;
import java.util.Objects;

import static org.assertj.core.api.Assertions.*;

class ShaclShapeValidatorTest {
	private final ShaclShapeValidator validator = new ShaclShapeValidator();

	@Test
	void test_classSupport() {
		Model model = ModelFactory.createDefaultModel();

		assertThat(validator.supports(model.getClass())).isTrue();
		assertThat(validator.supports(Model.class)).isTrue();

		assertThat(validator.supports(String.class)).isFalse();
		assertThat(validator.supports(Object.class)).isFalse();
	}

	@Test
	void when_ValidateValidShaclShape_thenReturnValid() throws URISyntaxException {
		final Model validShaclShape = readModelFromFile("eventstream/streams/valid-shape.ttl");

		assertThatNoException().isThrownBy(() -> validator.validate(validShaclShape));
	}

	@Test
	void when_validateInvalidShaclShape_thenReturnInvalid() throws URISyntaxException {
		final Model model = readModelFromFile("eventstream/streams/invalid-shape.ttl");

		assertThatThrownBy(() -> validator.validate(model)).isInstanceOf(LdesShaclValidationException.class);
	}

	private Model readModelFromFile(String fileName) throws URISyntaxException {
		ClassLoader classLoader = getClass().getClassLoader();
		String uri = Objects.requireNonNull(classLoader.getResource(fileName)).toURI().toString();
		return RDFDataMgr.loadModel(uri);
	}

}
