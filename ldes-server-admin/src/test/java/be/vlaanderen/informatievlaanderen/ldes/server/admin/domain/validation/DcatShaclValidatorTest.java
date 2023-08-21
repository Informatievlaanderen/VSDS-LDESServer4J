package be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.validation;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.spi.LdesShaclValidationException;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParser;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DcatShaclValidatorTest {
	private final DcatShaclValidator dcatShaclValidator = new DcatShaclValidator("dcat/dcat-ap_2.0.1_shacl_shapes.ttl");

	@Test
	void test_support() {
		assertTrue(dcatShaclValidator.supports(Model.class));
		assertTrue(dcatShaclValidator.supports(ModelFactory.createDefaultModel().getClass()));

		assertFalse(dcatShaclValidator.supports(String.class));
		assertFalse(dcatShaclValidator.supports(Resource.class));
	}

	@Test
	void when_ValidatorHasNoDcatConfigured_then_ThrowNothing() {
		DcatShaclValidator emptyValidator = new DcatShaclValidator("");

		Model dcat = RDFParser.source("dcat/invalid-to-provided-shape.ttl").lang(Lang.TURTLE).toModel();
		assertDoesNotThrow(() -> emptyValidator.validateShape(dcat));
	}

	@Test
	void when_ValidDcatProvided_then_ThrowNothing() {
		Model dcat = RDFParser.source("dcat/valid-to-provided-shape.ttl").lang(Lang.TURTLE).toModel();
		assertDoesNotThrow(() -> dcatShaclValidator.validateShape(dcat));
	}

	@Test
	void when_InvalidDcatProvided_then_ThrowException() {
		Model dcat = RDFParser.source("dcat/invalid-to-provided-shape.ttl").lang(Lang.TURTLE).toModel();
		assertThrows(LdesShaclValidationException.class, () -> dcatShaclValidator.validateShape(dcat));
	}
}