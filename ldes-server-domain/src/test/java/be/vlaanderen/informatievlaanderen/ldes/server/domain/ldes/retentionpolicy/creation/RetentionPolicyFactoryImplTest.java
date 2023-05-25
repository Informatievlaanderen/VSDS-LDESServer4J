package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.retentionpolicy.creation;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.retentionpolicy.RetentionPolicy;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.retentionpolicy.timebased.TimeBasedRetentionPolicy;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewSpecification;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.RDFDataMgr;
import org.junit.jupiter.api.Test;

import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

class RetentionPolicyFactoryImplTest {

	private final RetentionPolicyFactory retentionPolicyFactory = new RetentionPolicyFactoryImpl();

	@Test
	void when_RetentionPolicyDescriptionIsAValidTimeBasedRetentionPolicy_then_ATimeBasedRetentionPolicyIsReturned()
			throws URISyntaxException {
		ViewSpecification viewSpecification = new ViewSpecification();
		viewSpecification
				.setRetentionPolicies(List.of(readModelFromFile("retentionpolicy/timebased/valid_timebased.ttl")));

		List<RetentionPolicy> retentionPolicyListForView = retentionPolicyFactory
				.getRetentionPolicyListForView(viewSpecification);

		assertEquals(1, retentionPolicyListForView.size());
		assertTrue(retentionPolicyListForView.get(0) instanceof TimeBasedRetentionPolicy);
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