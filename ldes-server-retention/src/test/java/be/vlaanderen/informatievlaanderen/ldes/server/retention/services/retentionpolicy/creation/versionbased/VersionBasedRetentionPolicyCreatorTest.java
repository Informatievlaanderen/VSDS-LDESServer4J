package be.vlaanderen.informatievlaanderen.ldes.server.retention.services.retentionpolicy.creation.versionbased;

import be.vlaanderen.informatievlaanderen.ldes.server.retention.services.retentionpolicy.definition.RetentionPolicy;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.services.retentionpolicy.definition.versionbased.VersionBasedRetentionPolicy;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.RDFDataMgr;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.URISyntaxException;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class VersionBasedRetentionPolicyCreatorTest {

	private VersionBasedRetentionPolicyCreator versionBasedRetentionPolicyCreator;

	@BeforeEach
	void setUp() {
		this.versionBasedRetentionPolicyCreator = new VersionBasedRetentionPolicyCreator();
	}

	@Test
	void when_ModelDescribesAValidVersionBasedRetentionPolicy_then_AVersionBasedRetentionPolicyIsReturned()
			throws URISyntaxException {
		Model retentionModel = readModelFromFile("retentionpolicy/versionbased/valid_versionbased.ttl");

		RetentionPolicy retentionPolicy = versionBasedRetentionPolicyCreator.createRetentionPolicy(retentionModel);

        assertInstanceOf(VersionBasedRetentionPolicy.class, retentionPolicy);
	}

	@Test
	void when_ModelDoesNotExactlyHaveOneLdesAmountStatement_then_AnIllegalArgumentExceptionIsThrown()
			throws URISyntaxException {
		Model retentionModel = readModelFromFile("retentionpolicy/versionbased/invalid_versionbased.ttl");

		IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class,
				() -> versionBasedRetentionPolicyCreator.createRetentionPolicy(retentionModel));
		assertEquals(
				"Cannot Create Version Based Retention Policy in which there is not exactly 1 https://w3id.org/ldes#amount statement.\n"
						+
						" Found 2 statements in :\n" +
						"[ a                               <https://w3id.org/ldes#LatestVersionSubset>;\n" +
						"  <https://w3id.org/ldes#amount>  3 , 2\n" +
						"] .\n",
				illegalArgumentException.getMessage());
	}

	private Model readModelFromFile(String fileName) throws URISyntaxException {
		ClassLoader classLoader = getClass().getClassLoader();
		String uri = Objects.requireNonNull(classLoader.getResource(fileName)).toURI().toString();
		return RDFDataMgr.loadModel(uri);
	}
}