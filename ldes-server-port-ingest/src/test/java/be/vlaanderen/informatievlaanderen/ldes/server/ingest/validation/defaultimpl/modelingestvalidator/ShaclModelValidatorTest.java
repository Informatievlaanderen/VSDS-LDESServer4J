package be.vlaanderen.informatievlaanderen.ldes.server.ingest.validation.defaultimpl.modelingestvalidator;

import be.vlaanderen.informatievlaanderen.ldes.server.ingest.validation.IngestValidationException;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.RDFParser;
import org.apache.jena.shacl.Shapes;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ShaclModelValidatorTest {

	@Test
	void validate_shouldThrowException_whenModelInvalid() {
		Model shape = RDFParser.source("validation/example-shape.ttl").build().toModel();

		var validator = new ShaclModelValidator(Shapes.parse(shape));

		Model invalidModel = RDFParser.source("validation/example-data-invalid.ttl").build().toModel();
		assertThrows(IngestValidationException.class, () -> validator.validate(invalidModel));
	}

	@Test
	void validate_shouldNotThrowException_whenModelIsValid() {
		Model shape = RDFParser.source("validation/example-shape.ttl").build().toModel();

		var validator = new ShaclModelValidator(Shapes.parse(shape));

		Model invalidModel = RDFParser.source("validation/example-data.ttl").build().toModel();
		assertDoesNotThrow(() -> validator.validate(invalidModel));
	}

}