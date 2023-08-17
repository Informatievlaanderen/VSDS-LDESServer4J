package be.vlaanderen.informatievlaanderen.ldes.server.retention.services.retentionpolicy.creation;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.entities.ViewSpecification;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.repositories.MemberPropertiesRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.services.retentionpolicy.definition.RetentionPolicy;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.services.retentionpolicy.definition.pointintime.PointInTimeRetentionPolicy;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.services.retentionpolicy.definition.timebased.TimeBasedRetentionPolicy;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.services.retentionpolicy.definition.versionbased.VersionBasedRetentionPolicy;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.RDFDataMgr;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class RetentionPolicyFactoryImplTest {

	@Mock
	private MemberPropertiesRepository memberPropertiesRepository;

	private RetentionPolicyFactory retentionPolicyFactory;

	@BeforeEach
	void setUp() {
		retentionPolicyFactory = new RetentionPolicyFactoryImpl(memberPropertiesRepository);
	}

	@ParameterizedTest
	@ArgumentsSource(FileNameRetentionPolicyArgumentsProvider.class)
	void when_RetentionPolicyDescriptionIsAValidRetentionPolicy_then_ACorrectRetentionPolicyImplementationIsReturned(
			String fileName, Class<? extends RetentionPolicy> expectedRetentionPolicyClass)
			throws URISyntaxException {
		ViewName viewName = ViewName.fromString("col/view");
		var viewSpecification = new ViewSpecification(viewName, List.of(readModelFromFile(fileName)), List.of(),
				100);

		List<RetentionPolicy> retentionPolicyListForView = retentionPolicyFactory
				.getRetentionPolicyListForView(viewSpecification);

		assertEquals(1, retentionPolicyListForView.size());
		assertEquals(retentionPolicyListForView.get(0).getClass(), expectedRetentionPolicyClass);
	}

	static class FileNameRetentionPolicyArgumentsProvider implements ArgumentsProvider {

		@Override
		public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
			return Stream.of(
					Arguments.of("retentionpolicy/timebased/valid_timebased.ttl", TimeBasedRetentionPolicy.class),
					Arguments.of("retentionpolicy/versionbased/valid_versionbased.ttl",
							VersionBasedRetentionPolicy.class),
					Arguments.of("retentionpolicy/pointintime/valid_pointintime.ttl",
							PointInTimeRetentionPolicy.class));
		}
	}

	@Test
	void when_RetentionPolicyDescriptionDoesNotHaveASyntaxTypeStatement_then_AnIllegalArgumentExceptionIsThrown()
			throws URISyntaxException {
		ViewName viewName = ViewName.fromString("col/view");
		List<Model> retentionPolicies = List.of(readModelFromFile("retentionpolicy/retentionpolicy-without-type.ttl"));
		var viewSpecification = new ViewSpecification(viewName, retentionPolicies, List.of(), 100);

		IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class,
				() -> retentionPolicyFactory
						.getRetentionPolicyListForView(viewSpecification));
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
				() -> retentionPolicyFactory
						.getRetentionPolicyListForView(viewSpecification));
		assertEquals("Cannot Create Retention Policy from type: https://w3id.org/ldes#UnkownPolicy",
				illegalArgumentException.getMessage());
	}

	private Model readModelFromFile(String fileName) throws URISyntaxException {
		ClassLoader classLoader = getClass().getClassLoader();
		String uri = Objects.requireNonNull(classLoader.getResource(fileName)).toURI().toString();
		return RDFDataMgr.loadModel(uri);
	}
}