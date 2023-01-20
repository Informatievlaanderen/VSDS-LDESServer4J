package be.vlaanderen.informatievlaanderen.ldes.server.domain.validation;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.config.LdesConfig;
import org.apache.jena.graph.Graph;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParserBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class LdesShaclValidatorTest {

	private LdesShaclValidator ldesShaclValidator;

	@BeforeEach
	void setUp() throws URISyntaxException {
		LdesConfig ldesConfig = new LdesConfig();

		ldesConfig.setShape(getAbsolutePathOfFile("validation/example-shape.ttl"));
		ldesShaclValidator = new LdesShaclValidatorImpl(ldesConfig);
	}

	@Test
	void when_ValidateProvidedValidData_thenReturnValid() throws URISyntaxException, IOException {
		Graph dataGraph = readLdesMemberFromFile("validation/example-data.ttl").getGraph();

		assertTrue(ldesShaclValidator.validate(dataGraph));
	}

	@Test
	void when_ValidateProvidedInvalidData_thenReturnInvalid() throws URISyntaxException, IOException {
		Graph invalidDataGraph = readLdesMemberFromFile("validation/example-data-invalid.ttl").getGraph();

		assertFalse(ldesShaclValidator.validate(invalidDataGraph));
	}

	@Test
	void when_ValidateWithNoProvidedShape_thenReturnValid() throws URISyntaxException, IOException {
		ldesShaclValidator = new LdesShaclValidatorImpl(new LdesConfig());

		Graph dataGraph = readLdesMemberFromFile("validation/example-data.ttl").getGraph();
		Graph invalidDataGraph = readLdesMemberFromFile("validation/example-data-invalid.ttl").getGraph();

		assertTrue(ldesShaclValidator.validate(dataGraph));
		assertTrue(ldesShaclValidator.validate(invalidDataGraph));
	}

	private String getAbsolutePathOfFile(String fileName)
			throws URISyntaxException {
		return new File(Objects.requireNonNull(getClass().getClassLoader().getResource(fileName)).toURI()).getAbsolutePath();
	}

	private Model readLdesMemberFromFile(String fileName)
			throws URISyntaxException, IOException {
		ClassLoader classLoader = getClass().getClassLoader();
		File file = new File(Objects.requireNonNull(classLoader.getResource(fileName)).toURI());

		return RDFParserBuilder.create()
				.fromString(Files.lines(Paths.get(file.toURI())).collect(Collectors.joining())).lang(Lang.TTL)
				.toModel();
	}
}
