package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.retentionpolicy.creation;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.retentionpolicy.RetentionPolicy;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.retentionpolicy.timebased.TimeBasedRetentionPolicy;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.RDFDataMgr;
import org.junit.jupiter.api.Test;

import java.net.URISyntaxException;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

class TimeBasedRetentionPolicyCreatorTest {

	private final TimeBasedRetentionPolicyCreator timeBasedRetentionPolicyCreator = new TimeBasedRetentionPolicyCreator();

	@Test
	void when_ModelDescribesAValidTimeBasedRetentionPolicy_then_ATimeBasedRetentionPolicyIsReturned()
			throws URISyntaxException {
		Model retentionModel = readModelFromFile("retentionpolicy/timebased/valid_timebased.ttl");

		RetentionPolicy retentionPolicy = timeBasedRetentionPolicyCreator.createRetentionPolicy(retentionModel);

		assertTrue(retentionPolicy instanceof TimeBasedRetentionPolicy);
	}

	@Test
	void when_ModelDoesNotExactlyHaveOneTreeValueStatement_then_AnIllegalArgumentExceptionIsThrown()
			throws URISyntaxException {
		Model retentionModel = readModelFromFile("retentionpolicy/timebased/invalid_timebased.ttl");

		IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class,
				() -> timeBasedRetentionPolicyCreator.createRetentionPolicy(retentionModel));
		assertEquals(
				"Cannot Create Time Based Retention Policy in which there is not exactly 1 https://w3id.org/tree#value statement.\n"
						+
						" Found 2 statements in :\n" +
						"[ a                              <https://w3id.org/ldes#DurationAgoPolicy> ;\n" +
						"  <https://w3id.org/tree#value>  \"PT3M\"^^<http://www.w3.org/2001/XMLSchema#duration> , \"PT2M\"^^<http://www.w3.org/2001/XMLSchema#duration>\n"
						+
						"] .\n",
				illegalArgumentException.getMessage());
	}

	private Model readModelFromFile(String fileName) throws URISyntaxException {
		ClassLoader classLoader = getClass().getClassLoader();
		String uri = Objects.requireNonNull(classLoader.getResource(fileName)).toURI().toString();
		return RDFDataMgr.loadModel(uri);
	}
}