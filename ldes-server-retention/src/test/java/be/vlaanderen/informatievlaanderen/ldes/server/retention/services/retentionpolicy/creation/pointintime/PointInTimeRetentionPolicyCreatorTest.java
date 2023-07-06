package be.vlaanderen.informatievlaanderen.ldes.server.retention.services.retentionpolicy.creation.pointintime;

import be.vlaanderen.informatievlaanderen.ldes.server.retention.services.retentionpolicy.creation.RetentionPolicyCreator;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.services.retentionpolicy.definition.RetentionPolicy;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.services.retentionpolicy.definition.pointintime.PointInTimeRetentionPolicy;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.RDFDataMgr;
import org.junit.jupiter.api.Test;

import java.net.URISyntaxException;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

class PointInTimeRetentionPolicyCreatorTest {

	private final RetentionPolicyCreator retentionPolicyCreator = new PointInTimeRetentionPolicyCreator();

	@Test
	void when_ModelDescribesAValidPointInTimeRetentionPolicy_then_APointInTimeRetentionPolicyIsReturned()
			throws URISyntaxException {
		Model retentionModel = readModelFromFile("retentionpolicy/pointintime/valid_pointintime.ttl");

		RetentionPolicy retentionPolicy = retentionPolicyCreator.createRetentionPolicy(retentionModel);

		assertTrue(retentionPolicy instanceof PointInTimeRetentionPolicy);
	}

	@Test
	void when_ModelDoesNotExactlyHaveOneLdesPointInTimeStatement_then_AnIllegalArgumentExceptionIsThrown()
			throws URISyntaxException {
		Model retentionModel = readModelFromFile("retentionpolicy/pointintime/invalid_pointintime.ttl");

		IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class,
				() -> retentionPolicyCreator.createRetentionPolicy(retentionModel));
		assertEquals(
				"Cannot Create Point-In-Time Retention Policy in which there is not exactly 1 https://w3id.org/ldes#pointInTime statement.\n"
						+
						" Found 2 statements in :\n" +
						"[ a       <https://w3id.org/ldes#PointInTimePolicy> ;\n" +
						"  <https://w3id.org/ldes#pointInTime>\n" +
						"          \"2023-04-13T00:00:00\"^^<http://www.w3.org/2001/XMLSchema#dateTime> , \"2023-04-12T00:00:00\"^^<http://www.w3.org/2001/XMLSchema#dateTime>\n"
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