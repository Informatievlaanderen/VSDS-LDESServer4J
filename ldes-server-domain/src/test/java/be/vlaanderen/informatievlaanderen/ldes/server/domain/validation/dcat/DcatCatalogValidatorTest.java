package be.vlaanderen.informatievlaanderen.ldes.server.domain.validation.dcat;

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
	void should_NotThrowAnything_when_Valid() {
		final Model model = RDFParser.fromString(validServerDcat).lang(Lang.TURTLE).build().toModel();
		// noinspection DataFlowIssue
		assertDoesNotThrow(() -> validator.validate(model, null));
	}

	@ParameterizedTest(name = "Expected message: {0}")
	@ArgumentsSource(InvalidModelProvider.class)
	void should_ThrowIllegalArgumentException_when_Invalid(String expectedMessage, String turtleDcatString) {
		Model dcat = RDFParser.fromString(turtleDcatString).lang(Lang.TURTLE).build().toModel();
		// noinspection DataFlowIssue
		Exception e = assertThrows(IllegalArgumentException.class, () -> validator.validate(dcat, null));
		assertEquals(expectedMessage, e.getMessage());
	}

	static class InvalidModelProvider implements ArgumentsProvider {

		@Override
		public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
			return Stream.of(
					Arguments.of("Node of type dcat:DataCatalog must be a blank node", createModelCatalogWithIdentity()),
					Arguments.of("Model must include exactly one DataCatalog. Not more, not less.",
							createModelWithMultipleDcatCatalogs()),
					Arguments.of("Model cannot contain a relation to the dataset.",
							createModelWithServesDatasetPredicate()),
					Arguments.of("Model cannot contain a relation to the dataset.",
							createModelWithDatasetPredicate()),
					Arguments.of("Model cannot contain a dataset.", createModelWithADataset()),
					Arguments.of("Model cannot contain a dataset.", createModelWithADatasetReference()),
					Arguments.of("Model cannot contain a data service.", createModelWithADataService()));
		}

		private String createModelCatalogWithIdentity() {
			return validServerDcat.replace("[]", "<http://example.org/svc/1>");
		}

		private String createModelWithMultipleDcatCatalogs() {
			return validServerDcat + "\n\n" +
					"""
							    [] a dcat:Catalog ;
							      dct:title "My geo-spatial view"@en ;
							      dct:description "Geospatial fragmentation for my LDES"@en .
							""";
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
			return validServerDcat + "\n\n" +
					"""
							    [] a dcat:Dataset ;
							      dct:title "My dataset"@en ;
							      dct:description "Geospatial dataset for my LDES"@en .
							""";
		}

		private String createModelWithADataService() {
			return validServerDcat + "\n\n" +
					"""
							    [] a dcat:DataService ;
							      dct:title "My catalog"@en ;
							      dct:description "Geospatial catalog for my LDES"@en .
							""";
		}

	}
}