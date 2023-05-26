package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.retentionpolicy.creation;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.retentionpolicy.RetentionPolicy;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.retentionpolicy.timebased.TimeBasedRetentionPolicy;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.retentionpolicy.versionbased.VersionBasedRetentionPolicy;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.repository.MemberRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewSpecification;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class RetentionPolicyFactoryImplTest {

	@Mock
	private MemberRepository memberRepository;

	private RetentionPolicyFactory retentionPolicyFactory;

	@BeforeEach
	void setUp() {
		retentionPolicyFactory = new RetentionPolicyFactoryImpl(memberRepository);
	}

	@ParameterizedTest
	@ArgumentsSource(CoordinateZoomLevelArgumentsProvider.class)
	void when_RetentionPolicyDescriptionIsAValidTimeBasedRetentionPolicy_then_ATimeBasedRetentionPolicyIsReturned(
			String fileName, Class<? extends RetentionPolicy> expectedRetentionPolicyClass)
			throws URISyntaxException {
		ViewSpecification viewSpecification = new ViewSpecification();
		viewSpecification
				.setRetentionPolicies(List.of(readModelFromFile(fileName)));

		List<RetentionPolicy> retentionPolicyListForView = retentionPolicyFactory
				.getRetentionPolicyListForView(viewSpecification);

		assertEquals(1, retentionPolicyListForView.size());
		assertEquals(retentionPolicyListForView.get(0).getClass(), expectedRetentionPolicyClass);
	}

	static class CoordinateZoomLevelArgumentsProvider implements ArgumentsProvider {

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
		ViewSpecification viewSpecification = new ViewSpecification();
		viewSpecification
				.setRetentionPolicies(List.of(readModelFromFile("retentionpolicy/retentionpolicy-without-type.ttl")));

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
		ViewSpecification viewSpecification = new ViewSpecification();
		viewSpecification.setRetentionPolicies(
				List.of(readModelFromFile("retentionpolicy/retentionpolicy-with-unknown-type.ttl")));

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