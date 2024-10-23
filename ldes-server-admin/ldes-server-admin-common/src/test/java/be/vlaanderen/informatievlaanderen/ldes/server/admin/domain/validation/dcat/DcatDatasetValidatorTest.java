package be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.validation.dcat;

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

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;

class DcatDatasetValidatorTest {
	private DcatDatasetValidator validator;

	@BeforeEach
	void setUp() {
		validator = new DcatDatasetValidator();
	}

	@Test
	void test_support() {
		assertThat(validator.supports(Model.class)).isTrue();
		assertThat(validator.supports(String.class)).isFalse();
	}

	@Test
	void when_ValidModel_Then_Pass() {
		Model dcat = RDFParser.source("dcat/dataset/valid.ttl").lang(Lang.TURTLE).toModel();

		assertThatNoException().isThrownBy(() -> validator.validate(dcat));
	}

	@ParameterizedTest(name = "Expected message: {0}")
	@ArgumentsSource(InvalidArgumentsProvider.class)
	void when_InvalidModel_Then_throwException(String expectedMessage, String dcatString) {
		Model dcat = RDFParser.fromString(dcatString).lang(Lang.TURTLE).toModel();

		assertThatThrownBy(() -> validator.validate(dcat))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage(expectedMessage);
	}

	static class InvalidArgumentsProvider implements ArgumentsProvider {
		private final String validDcat;

		InvalidArgumentsProvider() throws IOException {
			validDcat = FileUtils.readFileToString(ResourceUtils.getFile("classpath:dcat/dataset/valid.ttl"),
					StandardCharsets.UTF_8);
		}

		@Override
		public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
			return Stream.of(
					Arguments.of("Node of type dcat:Dataset must be a blank node", createDatasetWithIdentity()),
					Arguments.of("Model must include exactly one dcat:Dataset. Not more, not less.",
							createModelWithMultipleDatasets()),
					Arguments.of("Model cannot contain a data catalog.", createModelWithADataCatalog()),
					Arguments.of("Model cannot contain any kind of relation to dcat:DataService.",
							createModelWithDataServiceReference()),
					Arguments.of("Model cannot contain any kind of relation to dcat:DataService.",
							createModelWithADataService()));
		}

		private String createDatasetWithIdentity() {
			return validDcat.replace("[]", "<http://example.org/svc/1>");
		}

		private String createModelWithMultipleDatasets() {
			return validDcat + "\n\n" +
					"""
							    [] a dcat:Dataset ;
							      dct:title "My geo-spatial view"@en ;
							      dct:description "Geospatial fragmentation for my LDES"@en .
							""";
		}

		private String createModelWithADataCatalog() {
			return validDcat + "\n\n" +
					"""
							    [] a dcat:Catalog ;
							      dct:title "My dataset"@en ;
							      dct:description "Geospatial dataset for my LDES"@en .
							""";
		}

		private String createModelWithDataServiceReference() {
			// injects reference before title and add statement existing model
			final String dataService = """
					<http://example.org/ds/1> a dcat:DataService ;
						dct:title "My data service"@en .
					""";
			return validDcat.replace("dct:title", "dcat:service <http://example.org/ds/1>;"
					+ "\ndct:title") + dataService;
		}

		private String createModelWithADataService() {
			return validDcat + "\n\n" +
					"""
							    [] a dcat:DataService ;
							      dct:title "My catalog"@en ;
							      dct:description "Geospatial catalog for my LDES"@en .
							""";
		}
	}

}
