package be.vlaanderen.informatievlaanderen.ldes.server.domain.validation;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.LdesShaclValidationException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.LdesConfig;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParserBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LdesShaclValidatorTest {

	private LdesShaclValidator ldesShaclValidator;

	@BeforeEach
	void setUp() throws URISyntaxException, IOException {
		LdesConfig specification = new LdesConfig();
		Model model = readLdesMemberFromFile("validation/example-shape.ttl").getModel();
		specification.validation().setEnabled(true);
		ldesShaclValidator = new LdesShaclValidator(model, specification);
	}

	@Test
	void when_ValidateProvidedValidData_thenReturnValid() throws URISyntaxException, IOException {
		Member validMember = readLdesMemberFromFile("validation/example-data.ttl");

		assertDoesNotThrow(() -> ldesShaclValidator.validateShape(validMember.getModel()));
	}

	@Test
	void when_ValidateProvidedInvalidData_thenReturnInvalid() throws URISyntaxException, IOException {
		Member invalidMember = readLdesMemberFromFile("validation/example-data-invalid.ttl");

		Model model = invalidMember.getModel();

		assertThrows(LdesShaclValidationException.class,
				() -> ldesShaclValidator.validateShape(model));
	}

	@Test
	void when_ValidateProvidedInvalidData_and_NoDbOrYamlShapeProvided_thenReturnValid()
			throws URISyntaxException, IOException {
		ldesShaclValidator = new LdesShaclValidator(null, new LdesConfig());

		Member validMember = readLdesMemberFromFile("validation/example-data.ttl");
		assertDoesNotThrow(() -> ldesShaclValidator.validateShape(validMember.getModel()));

		Member invalidMember = readLdesMemberFromFile("validation/example-data-invalid.ttl");
		assertDoesNotThrow(() -> ldesShaclValidator.validateShape(invalidMember.getModel()));
	}

	@Test
	void when_ValidateProvidedInvalidData_and_BothYamlAndDbShapeProvided_thenReturnInvalid()
			throws URISyntaxException, IOException {
		Model shape = readModelFromFile("validation/example-shape.ttl", Lang.TURTLE);

		LdesConfig config = new LdesConfig();
		LdesConfig.Validation validation = new LdesConfig.Validation();
		validation.setShape("validation/example-shape.ttl");
		config.setValidation(validation);

		ldesShaclValidator = new LdesShaclValidator(shape, config);

		Member invalidMember = readLdesMemberFromFile("validation/example-data-invalid.ttl");
		Model invalidModel = invalidMember.getModel();
		assertThrows(LdesShaclValidationException.class, () -> ldesShaclValidator.validateShape(invalidModel));
	}

	@Test
	void when_ValidateProvidedInvalidData_and_OnlyYamlShapeProvided_thenReturnInvalid()
			throws URISyntaxException, IOException {
		LdesConfig config = new LdesConfig();
		LdesConfig.Validation validation = new LdesConfig.Validation();
		validation.setShape("validation/example-shape.ttl");
		config.setValidation(validation);

		ldesShaclValidator = new LdesShaclValidator(config);

		Member invalidMember = readLdesMemberFromFile("validation/example-data-invalid.ttl");
		Model invalidModel = invalidMember.getModel();
		assertThrows(LdesShaclValidationException.class, () -> ldesShaclValidator.validateShape(invalidModel));
	}

	@Test
	void when_ValidateProvidedInvalidData_and_OnlyDbShapeProvided_thenReturnInvalid()
			throws URISyntaxException, IOException {
		Model shape = readModelFromFile("validation/example-shape.ttl", Lang.TURTLE);

		ldesShaclValidator = new LdesShaclValidator(shape, new LdesConfig());

		Member invalidMember = readLdesMemberFromFile("validation/example-data-invalid.ttl");
		Model invalidModel = invalidMember.getModel();
		assertThrows(LdesShaclValidationException.class, () -> ldesShaclValidator.validateShape(invalidModel));
	}

	@Test
	void when_ValidateWithValidationNotEnabled_thenReturnValid() throws URISyntaxException, IOException {
		LdesConfig ldesConfig = new LdesConfig();
		ldesConfig.validation().setEnabled(false);
		ldesShaclValidator = new LdesShaclValidator(ldesConfig);

		Member validMember = readLdesMemberFromFile("validation/example-data.ttl");
		assertDoesNotThrow(() -> ldesShaclValidator.validateShape(validMember.getModel()));

		Member invalidMember = readLdesMemberFromFile("validation/example-data-invalid.ttl");
		assertDoesNotThrow(() -> ldesShaclValidator.validateShape(invalidMember.getModel()));
	}

	private Member readLdesMemberFromFile(String fileName)
			throws URISyntaxException, IOException {
		ClassLoader classLoader = getClass().getClassLoader();
		File file = new File(Objects.requireNonNull(classLoader.getResource(fileName)).toURI());

		Model m = RDFParserBuilder.create()
				.fromString(Files.lines(Paths.get(file.toURI())).collect(Collectors.joining())).lang(Lang.TTL)
				.toModel();
		return new Member("", "collectionName", 0L, null, null, m, new ArrayList<>());
	}

	private Model readModelFromFile(String fileName, Lang lang) throws URISyntaxException, IOException {
		ClassLoader classLoader = getClass().getClassLoader();
		URI uri = Objects.requireNonNull(classLoader.getResource(fileName)).toURI();

		return RDFParserBuilder.create()
				.fromString(Files.lines(Paths.get(uri)).collect(Collectors.joining())).lang(lang)
				.toModel();
	}
}
