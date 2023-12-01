package be.vlaanderen.informatievlaanderen.ldes.server.retention.services.retentionpolicy.creation;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewSpecification;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.services.retentionpolicy.definition.RetentionPolicy;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.services.retentionpolicy.definition.timeandversionbased.TimeAndVersionBasedRetentionPolicy;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.services.retentionpolicy.definition.timebased.TimeBasedRetentionPolicy;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.services.retentionpolicy.definition.versionbased.VersionBasedRetentionPolicy;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.RDFDataMgr;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class RetentionPolicyFactoryImplTest {

	private final RetentionPolicyFactory retentionPolicyFactory = new RetentionPolicyFactoryImpl();

	@ParameterizedTest
	@ArgumentsSource(FileNameRetentionPolicyArgumentsProvider.class)
	void when_RetentionPolicyDescriptionIsAValidRetentionPolicy_then_ACorrectRetentionPolicyImplementationIsReturned(
			String fileName, Class<? extends RetentionPolicy> expectedRetentionPolicyClass)
			throws URISyntaxException {
		ViewName viewName = ViewName.fromString("col/view");
		var viewSpecification = new ViewSpecification(viewName, List.of(readModelFromFile(fileName)), List.of(),
				100);

		Optional<RetentionPolicy> result = retentionPolicyFactory.extractRetentionPolicy(viewSpecification);

		assertThat(result).isNotEmpty().containsInstanceOf(expectedRetentionPolicyClass);
	}

	static class FileNameRetentionPolicyArgumentsProvider implements ArgumentsProvider {

		@Override
		public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
			return Stream.of(
					Arguments.of("retentionpolicy/timebased/valid_timebased.ttl", TimeBasedRetentionPolicy.class),
					Arguments.of("retentionpolicy/versionbased/valid_versionbased.ttl",
							VersionBasedRetentionPolicy.class));
		}
	}

	@Test
	void when_RetentionPolicyDescriptionDoesNotHaveASyntaxTypeStatement_then_AnIllegalArgumentExceptionIsThrown()
			throws URISyntaxException {
		ViewName viewName = ViewName.fromString("col/view");
		List<Model> retentionPolicies = List.of(readModelFromFile("retentionpolicy/retentionpolicy-without-type.ttl"));
		var viewSpecification = new ViewSpecification(viewName, retentionPolicies, List.of(), 100);

		IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class,
				() -> retentionPolicyFactory.extractRetentionPolicy(viewSpecification));
		assertEquals("Cannot Extract Retention Policy from statements:\n" +
				"[ <https://w3id.org/tree#value>  \"PT2M\"^^<http://www.w3.org/2001/XMLSchema#duration> ] .\n",
				illegalArgumentException.getMessage());
	}

	@Test
	void when_RetentionPolicyDescriptionHasAnUnknownType_then_AnIllegalArgumentExceptionIsThrown()
			throws URISyntaxException {
		ViewName viewName = ViewName.fromString("col/view");
		List<Model> retentionPolicies = List
				.of(readModelFromFile("retentionpolicy/retentionpolicy-with-unknown-type.ttl"));
		var viewSpecification = new ViewSpecification(viewName, retentionPolicies, List.of(), 100);

		IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class,
				() -> retentionPolicyFactory.extractRetentionPolicy(viewSpecification));
		assertEquals("Cannot Create Retention Policy from type: https://w3id.org/ldes#UnkownPolicy",
				illegalArgumentException.getMessage());
	}

	@Test
	void when_ViewHasNoRetentionPolicies_then_EmptyIsReturned() {
		ViewName viewName = ViewName.fromString("col/view");
		var viewSpecification = new ViewSpecification(viewName, List.of(), List.of(), 100);

		Optional<RetentionPolicy> result = retentionPolicyFactory.extractRetentionPolicy(viewSpecification);

		assertThat(result).isEmpty();
	}

	@Test
	void when_ViewHasMoreThan2RetentionPolicies_then_ExceptionIsThrown() throws URISyntaxException {
		ViewName viewName = ViewName.fromString("col/view");
		Model policyModel = readModelFromFile("retentionpolicy/timebased/valid_timebased.ttl");
		var viewSpecification = new ViewSpecification(viewName, List.of(policyModel, policyModel, policyModel),
				List.of(), 100);

		IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class,
				() -> retentionPolicyFactory.extractRetentionPolicy(viewSpecification));
		assertEquals("A view cannot have more than 2 retention policies!",
				illegalArgumentException.getMessage());
	}

	@Test
	void when_ViewContainsTimeBasedAndVersionPolicies_then_ATimeAndVersionBasedRetentionPolicyIsReturned() throws URISyntaxException {
		ViewName viewName = ViewName.fromString("col/view");
		Model timeBasedPolicy = readModelFromFile("retentionpolicy/timebased/valid_timebased.ttl");
		Model versionBasedPolicy = readModelFromFile("retentionpolicy/versionbased/valid_versionbased.ttl");
		List<Model> models = List.of(timeBasedPolicy, versionBasedPolicy);
		var viewSpecification = new ViewSpecification(viewName, models, List.of(), 100);

		Optional<RetentionPolicy> result = retentionPolicyFactory.extractRetentionPolicy(viewSpecification);

		assertThat(result).isNotEmpty().containsInstanceOf(TimeAndVersionBasedRetentionPolicy.class);
	}

	private Model readModelFromFile(String fileName) throws URISyntaxException {
		ClassLoader classLoader = getClass().getClassLoader();
		String uri = Objects.requireNonNull(classLoader.getResource(fileName)).toURI().toString();
		return RDFDataMgr.loadModel(uri);
	}
}
