package be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.validation;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.ShaclValidationException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.validators.DcatShaclValidator;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParser;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class DcatShaclValidatorTest {
	private final DcatShaclValidator dcatShaclValidator = new DcatShaclValidator("dcat/dcat-ap_2.0.1_shacl_shapes.ttl");

	@Test
	void test_support() {
		assertThat(dcatShaclValidator.supports(Model.class)).isTrue();
		assertThat(dcatShaclValidator.supports(ModelFactory.createDefaultModel().getClass())).isTrue();

		assertThat(dcatShaclValidator.supports(String.class)).isFalse();
		assertThat(dcatShaclValidator.supports(Resource.class)).isFalse();
	}

	@Test
	void when_ValidatorHasNoDcatConfigured_then_ThrowNothing() {
		DcatShaclValidator emptyValidator = new DcatShaclValidator("");

		Model dcat = RDFParser.source("dcat/invalid-to-provided-shape.ttl").lang(Lang.TURTLE).toModel();

		assertThatNoException().isThrownBy(() -> emptyValidator.validate(dcat));
	}

	@Test
	void when_ValidDcatProvided_then_ThrowNothing() {
		Model dcat = RDFParser.source("dcat/valid-to-provided-shape.ttl").lang(Lang.TURTLE).toModel();

		assertThatNoException().isThrownBy(() -> dcatShaclValidator.validate(dcat));
	}

	@Test
	void when_InvalidDcatProvided_then_ThrowException() {
		Model dcat = RDFParser.source("dcat/invalid-to-provided-shape.ttl").lang(Lang.TURTLE).toModel();

		assertThatThrownBy(() -> dcatShaclValidator.validate(dcat))
				.isInstanceOf(ShaclValidationException.class);
	}
}