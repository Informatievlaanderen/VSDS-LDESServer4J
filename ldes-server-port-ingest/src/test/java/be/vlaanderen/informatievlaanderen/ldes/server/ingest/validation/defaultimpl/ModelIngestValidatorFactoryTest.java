package be.vlaanderen.informatievlaanderen.ldes.server.ingest.validation.defaultimpl;

import be.vlaanderen.informatievlaanderen.ldes.server.ingest.validation.IngestValidationException;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.validation.defaultimpl.modelingestvalidator.ModelIngestValidator;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.RDFParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ModelIngestValidatorFactoryTest {

	private ModelIngestValidatorFactory factory;

	@BeforeEach
	void setUp() {
		factory = new ModelIngestValidatorFactory();
	}

	@Nested
	class CreateValidator {
		@Test
		void shouldReturnEmpty_whenNoShapeProvided() {
			ModelIngestValidator validator = factory.createValidator(null);

			assertDoesNotThrow(() -> validator.validate(ModelFactory.createDefaultModel()));
		}

		@Test
		void shouldReturnEmpty_whenEmptyShapeProvided() {
			ModelIngestValidator validator = factory.createValidator(ModelFactory.createDefaultModel());

			assertDoesNotThrow(() -> validator.validate(ModelFactory.createDefaultModel()));
		}

		@Test
		void shouldReturnValidatorFromProvidedShape_whenNotNull() {
			Model shape = RDFParser.source("validation/example-shape.ttl").build().toModel();

			ModelIngestValidator validator = factory.createValidator(shape);

			Model invalidModel = RDFParser.source("validation/example-data-invalid.ttl").build().toModel();
			assertThrows(IngestValidationException.class, () -> validator.validate(invalidModel));
		}
	}

}