package be.vlaanderen.informatievlaanderen.ldes.server.domain.validation.dcat;

import org.apache.commons.io.FileUtils;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class DcatDataServiceValidatorTest {
	private DcatDataServiceValidator validator;

	@BeforeEach
	void setUp() {
		validator = new DcatDataServiceValidator();
	}

	@Test
	void should_NotThrowAnything_when_Valid() {
		final Model model = RDFParser.source("validation/valid-dcat-service.ttl").lang(Lang.TURTLE).build().toModel();
		assertDoesNotThrow(() -> validator.validate(model));
	}

	@ParameterizedTest(name = "{0}")
	@ArgumentsSource(InvalidModelProvider.class)
	void should_ThrowIllegalArgumentException_when_Invalid(String name, String turtleDcatString) {
		assertNotNull(name);
		Model dcat = RDFParser.fromString(turtleDcatString).lang(Lang.TURTLE).build().toModel();
		assertThrows(IllegalArgumentException.class, () -> validator.validate(dcat));
	}

	static class InvalidModelProvider implements ArgumentsProvider {
		private final String validDcatView;

		InvalidModelProvider() throws IOException {
			this.validDcatView = readDcatFromFile();
		}

		@Override
		public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
			return Stream.of(
					Arguments.of("should_ThrowException_when_DataServiceHasIdentity", createDataServiceWithIdentity()),
					Arguments.of("should_ThrowException_when_ModelHasMultipleDataServices",
							createModelWithMultipleDataServices()),
					Arguments.of("should_ThrowException_when_ModelHasServesDatasetPredicate",
							createModelWithServesDatasetPredicate()),
					Arguments.of("should_ThrowException_when_ModelHasADataset", createModelWithADataset()),
					Arguments.of("should_ThrowException_when_ModelHasACatalog", createModelWithACatalog()));
		}

		private String createDataServiceWithIdentity() {
			return validDcatView.replace("[]", "<http://example.org/svc/1>");
		}

		private String createModelWithMultipleDataServices() {
			return validDcatView + "\n\n" + validDcatView;
		}

		private String createModelWithServesDatasetPredicate() {
			// injects statement before title.
			return validDcatView.replace("dct:title", "dcat:servesDataset <http://example.org/ds/1>;"
					+ "\ndct:title");
		}

		private String createModelWithADataset() {
			final String dcatDataset = """
					[] a dcat:Dataset ;
					  dct:title "My dataset"@en ;
					  dct:description "Geospatial dataset for my LDES"@en .
					""";
			return validDcatView + "\n\n" + dcatDataset;
		}

		private String createModelWithACatalog() {
			final String dcatCatalog = """
					[] a dcat:Catalog ;
					  dct:title "My dataset"@en ;
					  dct:description "Geospatial dataset for my LDES"@en .
					""";
			return validDcatView + "\n\n" + dcatCatalog;
		}

		private String readDcatFromFile() throws IOException {
			File file = ResourceUtils.getFile("classpath:validation/valid-dcat-service.ttl");
			return FileUtils.readFileToString(file, StandardCharsets.UTF_8);
		}

	}

}