package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.propertypath;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParser;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.util.List;
import java.util.stream.Stream;

import static org.apache.commons.lang3.ObjectUtils.isNotEmpty;
import static org.apache.jena.rdf.model.ResourceFactory.createPlainLiteral;
import static org.apache.jena.rdf.model.ResourceFactory.createResource;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class PropertyPathExtractorTest {

	@ParameterizedTest
	@ArgumentsSource(PropertyPathExtractorProvider.class)
	void testGetKey(String testName, String input, String propertyPath, RDFNode expectedResult,
			int expectedResultCount) {
		assertNotNull(testName);
		Model model = RDFParser.fromString(input).lang(Lang.NQUADS).build().toModel();
		PropertyExtractor extractor = PropertyPathExtractor.from(propertyPath);

		List<RDFNode> results = extractor.getProperties(model);

		assertEquals(expectedResultCount, results.size());
		if (isNotEmpty(results)) {
			assertEquals(expectedResult, results.get(0));
		}
	}

	static class PropertyPathExtractorProvider implements ArgumentsProvider {
		@Override
		public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
			return Stream.of(
					Arguments.of(
							"shouldReturnLiteralStringWhenLinkedPath",
							"""
									    <https://example.com/hindrances/29797> <https://data.com/ns/mobiliteit#zone> <https://example.com/hindrances/zones/a> .
									    <https://example.com/hindrances/zones/a> <https://data.com/ns/mobiliteit#Zone.type> 'my-zone-type' .
									""",
							"<https://data.com/ns/mobiliteit#zone>/<https://data.com/ns/mobiliteit#Zone.type>",
							createPlainLiteral("my-zone-type"),
							1),
					Arguments.of(
							"shouldReturnUriAsResourceWhenObjectIsResource",
							"<https://example.com/hindrances/29797> <https://data.com/ns/mobiliteit#zone> <https://example.com/hindrances/zones/a> .",
							"<https://data.com/ns/mobiliteit#zone>",
							createResource("https://example.com/hindrances/zones/a"),
							1),
					Arguments.of(
							"shouldReturnMultipleResultsWhenMultipleResultsMatch",
							"""
									    <https://example.com/hindrances/29797> <https://data.com/ns/mobiliteit#zone> <https://example.com/hindrances/zones/a> .
									    <https://example.com/hindrances/29798> <https://data.com/ns/mobiliteit#zone> <https://example.com/hindrances/zones/a> .
									""",
							"<https://data.com/ns/mobiliteit#zone>",
							createResource("https://example.com/hindrances/zones/a"),
							2),
					Arguments.of(
							"shouldReturnEmptyIfPathIsNotFound",
							"<https://example.com/hindrances/29797> <https://data.com/ns/mobiliteit#zone> <https://example.com/hindrances/zones/a> .",
							"<https://not-existing>",
							null,
							0),
					Arguments.of(
							"shouldReturnLiteralStringWhenSimplePath",
							"<https://example.com/hindrances/29797> <https://data.com/ns/mobiliteit#zone> 'my-zone-type' .",
							"<https://data.com/ns/mobiliteit#zone>",
							createPlainLiteral("my-zone-type"),
							1));
		}
	}

}