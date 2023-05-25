package be.vlaanderen.informatievlaanderen.ldes.server.domain.view.service;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.riot.RDFDataMgr;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class RetentionStatementsExtractorTest {
	private final RetentionStatementsExtractor retentionStatementsExtractor = new RetentionStatementsExtractor();

	@ParameterizedTest
	@ArgumentsSource(EventStreamArgumentProvider.class)
	void test(String fileName, int expectedNumberOfPolicies, List<Integer> expectedNumberOfStatementsOfPolicies)
			throws URISyntaxException {
		Model viewModel = readModelFromFile(fileName);

		List<Model> retentionPolicyMap = retentionStatementsExtractor.extractRetentionStatements(viewModel);

		assertEquals(expectedNumberOfPolicies, retentionPolicyMap.size());
		for (int i = 0; i < expectedNumberOfPolicies; i++) {
			Integer expectedNumberOfStatementsOfPolicy = expectedNumberOfStatementsOfPolicies.get(i);
			assertEquals(expectedNumberOfStatementsOfPolicy,
					retentionPolicyMap.get(i).listStatements().toList().size());
		}
	}

	private Model readModelFromFile(String fileName) throws URISyntaxException {
		ClassLoader classLoader = getClass().getClassLoader();
		String uri = Objects.requireNonNull(classLoader.getResource(fileName)).toURI()
				.toString();
		return RDFDataMgr.loadModel(uri);
	}

	static class EventStreamArgumentProvider implements ArgumentsProvider {
		@Override
		public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) {
			return Stream.of(
					Arguments.of("viewconverter/retentionpolicies/timebased_retentionpolicy.ttl", 1, List.of(2)),
					Arguments.of("viewconverter/retentionpolicies/versionbased_retentionpolicy.ttl", 1, List.of(2)),
					Arguments.of("viewconverter/retentionpolicies/pointintime_retentionpolicy.ttl", 1, List.of(2)),
					Arguments.of("viewconverter/retentionpolicies/fictional_retentionpolicy.ttl", 1, List.of(6)),
					Arguments.of("viewconverter/retentionpolicies/multiple_retentionpolicies.ttl", 3,
							List.of(2, 2, 2)));
		}
	}
}