package be.vlaanderen.informatievlaanderen.ldes.server.ingest.validation.defaultimpl;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.AppConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.LdesConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.validation.IngestValidationException;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.RDFParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ModelIngestValidatorFactoryTest {

	private static final String COLLECTION_NAME = "collectionName";

	private ModelIngestValidatorFactory factory;
	private LdesConfig.Validation validation;

	@BeforeEach
	void setUp() {
		validation = new LdesConfig.Validation();
		final LdesConfig ldesConfig = new LdesConfig();
		ldesConfig.setValidation(validation);
		ldesConfig.setCollectionName(COLLECTION_NAME);
		final AppConfig appConfig = new AppConfig();
		appConfig.setCollections(List.of(ldesConfig));
		factory = new ModelIngestValidatorFactory(appConfig);
	}

	@Nested
	class CreateValidator {
		@Test
		void shouldReturnEmpty_whenValidationIsDisabled() {
			validation.setShape("validation/example-shape.ttl");
			validation.setEnabled(false);

			ModelIngestValidator validator = factory.createValidator(null, COLLECTION_NAME);

			assertDoesNotThrow(() -> validator.validate(ModelFactory.createDefaultModel()));
		}

		@Test
		void shouldReturnValidatorFromProvidedShape_whenNotNull() {
			validation.setShape(null);
			Model shape = RDFParser.source("validation/example-shape.ttl").build().toModel();

			ModelIngestValidator validator = factory.createValidator(shape, COLLECTION_NAME);

			Model invalidModel = RDFParser.source("validation/example-data-invalid.ttl").build().toModel();
			assertThrows(IngestValidationException.class, () -> validator.validate(invalidModel));
		}

		@Test
		void shouldReturnValidatorFromConfig_whenProvidedShapeNullAndConfigNotNull() {
			validation.setShape("validation/example-shape.ttl");

			ModelIngestValidator validator = factory.createValidator(null, COLLECTION_NAME);

			Model invalidModel = RDFParser.source("validation/example-data-invalid.ttl").build().toModel();
			assertThrows(IngestValidationException.class, () -> validator.validate(invalidModel));
		}

		@Test
		void shouldReturnEmptyValidator_whenNoValidationShapePresent() {
			validation.setEnabled(true);

			ModelIngestValidator validator = factory.createValidator(null, COLLECTION_NAME);

			assertDoesNotThrow(() -> validator.validate(ModelFactory.createDefaultModel()));
		}
	}

}