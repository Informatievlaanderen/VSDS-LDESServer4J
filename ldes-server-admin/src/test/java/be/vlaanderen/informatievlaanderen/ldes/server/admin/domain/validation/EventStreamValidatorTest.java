package be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.validation;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.ShaclValidationException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.EventStream;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.validators.ModelValidator;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.validators.ShaclValidator;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.RDFDataMgr;
import org.junit.jupiter.api.Test;

import java.net.URISyntaxException;
import java.util.Objects;

import static org.assertj.core.api.Assertions.*;

class EventStreamValidatorTest {

	private final ModelValidator validator = new ShaclValidator("validator-shapes/eventstreamShaclShape.ttl");

	@Test
	void test_support() {
		Model model = ModelFactory.createDefaultModel();

		assertThat(validator.supports(model.getClass())).isTrue();
		assertThat(validator.supports(Model.class)).isTrue();

		assertThat(validator.supports(EventStream.class)).isFalse();
		assertThat(validator.supports(Integer.class)).isFalse();
	}

	@Test
	void when_validLdesProvided_then_returnValid() throws URISyntaxException {
		Model model = readModelFromFile("eventstream/streams/valid-ldes.ttl");

		assertThatNoException().isThrownBy(() -> validator.validate(model));
	}

	@Test
	void when_invalidLdesProvided_then_returnInvalid() throws URISyntaxException {
		Model model = readModelFromFile("eventstream/streams/invalid-shape.ttl");

		assertThatThrownBy(() -> validator.validate(model)).isInstanceOf(ShaclValidationException.class);
	}

	private Model readModelFromFile(String fileName) throws URISyntaxException {
		ClassLoader classLoader = getClass().getClassLoader();
		String uri = Objects.requireNonNull(classLoader.getResource(fileName)).toURI().toString();
		return RDFDataMgr.loadModel(uri);
	}
}