package be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.validation.dcat;

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

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class DcatCatalogValidatorTest {
	private final static String validServerDcat = """
							@prefix dct:   <http://purl.org/dc/terms/> .
							@prefix dcat:  <http://www.w3.org/ns/dcat#> .

							[] a dcat:Catalog ;
									dct:title "My geo-spatial view"@en ;
									dct:description "Geospatial fragmentation for my LDES"@en .
			""";

	private DcatCatalogValidator validator;

	@BeforeEach
	void setUp() {
		validator = new DcatCatalogValidator();
	}

	@Test
	void test_support() {
		assertTrue(validator.supports(Model.class));
		assertFalse(validator.supports(String.class));
	}

	@Test
	void when_Valid_then_ThrowNothing() {
		final Model model = RDFParser.fromString(validServerDcat).lang(Lang.TURTLE).build().toModel();
		assertDoesNotThrow(() -> validator.validate(model, null));
	}

	@ParameterizedTest(name = "Expected message: {0}")
	@ArgumentsSource(InvalidModelProvider.class)
	void when_Invalid_then_ThrowIllegalArgumentException(String expectedMessage, String turtleDcatString) {
		Model dcat = RDFParser.fromString(turtleDcatString).lang(Lang.TURTLE).build().toModel();
		Exception e = assertThrows(IllegalArgumentException.class, () -> validator.validate(dcat));
		assertEquals(expectedMessage, e.getMessage());
	}

	static class InvalidModelProvider implements ArgumentsProvider {

		@Override
		public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
			return Stream.of(
					Arguments.of("Node of type dcat:Catalog must be a blank node",
							createModelCatalogWithIdentity()),
					Arguments.of("Model must include exactly one dcat:Catalog. Not more, not less.",
							createModelWithMultipleDcatCatalogs()),
					Arguments.of("Model cannot contain any kind of relation to dcat:Dataset.",
							createModelWithServesDatasetPredicate()),
					Arguments.of("Model cannot contain any kind of relation to dcat:Dataset.",
							createModelWithDatasetPredicate()),
					Arguments.of("Model cannot contain any kind of relation to dcat:Dataset.",
							createModelWithADataset()),
					Arguments.of("Model cannot contain any kind of relation to dcat:Dataset.",
							createModelWithADatasetReference()),
					Arguments.of("Model cannot contain any kind of relation to dcat:DataService.",
							createModelWithDataServicePredicate()),
					Arguments.of("Model cannot contain any kind of relation to dcat:DataService.",
							createModelWithADataService()));
		}

		private String createModelCatalogWithIdentity() {
			return validServerDcat.replace("[]", "<http://example.org/svc/1>");
		}

		private String createModelWithMultipleDcatCatalogs() {
			final String catalog = """
					 [] a dcat:Catalog ;
					  dct:title "My geo-spatial view"@en ;
					  dct:description "Geospatial fragmentation for my LDES"@en .
					""";
			return validServerDcat + "\n\n" + catalog;
		}

		private String createModelWithServesDatasetPredicate() {
			// injects statement before title.
			return validServerDcat.replace("dct:title", "dcat:servesDataset <http://example.org/ds/1>;"
					+ "\ndct:title");
		}

		private String createModelWithDatasetPredicate() {
			// injects statement before title.
			return validServerDcat.replace("dct:title", "dcat:dataset <http://example.org/ds/1>;"
					+ "\ndct:title");
		}

		private String createModelWithDataServicePredicate() {
			// injects statement before title.
			return validServerDcat.replace("dct:title", "dcat:service <http://example.org/ds/1>;"
					+ "\ndct:title");
		}

		private String createModelWithADatasetReference() {
			// injects reference before title and dataset after
			final String dataset = """
					<http://example.org/ds/1> a dcat:Dataset;
						dct:title "My dataset"@en .
					""";
			return validServerDcat.replace("dct:title", "dcat:dataset <http://example.org/ds/1>;"
					+ "\ndct:title") + dataset;
		}

		private String createModelWithADataset() {
			final String dataset = """
					[] a dcat:Dataset ;
					  dct:title "My dataset"@en ;
					  dct:description "Geospatial dataset for my LDES"@en .
					""";
			return validServerDcat + "\n\n" + dataset;
		}

		private String createModelWithADataService() {
			final String dataService = """
					[] a dcat:DataService ;
					  dct:title "My catalog"@en ;
					  dct:description "Geospatial catalog for my LDES"@en .
					""";
			return validServerDcat + "\n\n" + dataService;
		}

	}
}
