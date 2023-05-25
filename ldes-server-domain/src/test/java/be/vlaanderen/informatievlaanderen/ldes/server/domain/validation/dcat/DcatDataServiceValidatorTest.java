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

class DcatDataServiceValidatorTest {
	private final static String validDcatView = """
							@prefix dct:   <http://purl.org/dc/terms/> .
							@prefix dcat:  <http://www.w3.org/ns/dcat#> .
							@prefix foaf:  <http://xmlns.com/foaf/0.1/> .
							@prefix org:   <http://www.w3.org/ns/org#> .
							@prefix legal: <http://www.w3.org/ns/legal#> .
							@prefix m8g:   <http://data.europa.eu/m8g/>
							@prefix locn:  <http://www.w3.org/ns/locn#>
							@prefix skos:  <http://www.w3.org/2004/02/skos/core#>

							[] a dcat:DataService ;
									dct:title "My geo-spatial view"@en ;
									dct:description "Geospatial fragmentation for my LDES"@en ;
									dct:license [
											a dct:LicenseDocument ;
											dct:type [
													a skos:Concept;
													skos:prefLabel "some public license"@en
											]
									] .
			""";

	private DcatDataServiceValidator validator;

	@BeforeEach
	void setUp() {
		validator = new DcatDataServiceValidator();
	}

	@Test
	void should_NotThrowAnything_when_Valid() {
		final Model model = RDFParser.fromString(validDcatView).lang(Lang.TURTLE).build().toModel();
		// noinspection DataFlowIssue
		assertDoesNotThrow(() -> validator.validate(model, null));
	}

	@ParameterizedTest
	@ArgumentsSource(InvalidModelProvider.class)
	void should_ThrowIllegalArgumentException_when_Invalid(String name, String turtleDcatString) {
		assertNotNull(name);
		Model dcat = RDFParser.fromString(turtleDcatString).lang(Lang.TURTLE).build().toModel();
		// noinspection DataFlowIssue
		assertThrows(IllegalArgumentException.class, () -> validator.validate(dcat, null));
	}

	static class InvalidModelProvider implements ArgumentsProvider {

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
			return validDcatView + "\n\n" +
					"""
							    [] a dcat:DataService ;
							      dct:title "My geo-spatial view"@en ;
							      dct:description "Geospatial fragmentation for my LDES"@en ;
							      dct:license [
							        a dct:LicenseDocument ;
							        dct:type [
							          a skos:Concept;
							          skos:prefLabel "some public license"@en
							        ]
							      ] .
							""";
		}

		private String createModelWithServesDatasetPredicate() {
			// injects statement before title.
			return validDcatView.replace("dct:title", "dcat:servesDataset <http://example.org/ds/1>;"
					+ "\ndct:title");
		}

		private String createModelWithADataset() {
			return validDcatView + "\n\n" +
					"""
							    [] a dcat:Dataset ;
							      dct:title "My dataset"@en ;
							      dct:description "Geospatial dataset for my LDES"@en .
							""";
		}

		private String createModelWithACatalog() {
			return validDcatView + "\n\n" +
					"""
							    [] a dcat:Catalog ;
							      dct:title "My catalog"@en ;
							      dct:description "Geospatial catalog for my LDES"@en .
							""";
		}

	}

}