package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.retentionpolicy.creation;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.retentionpolicy.RetentionPolicy;
import org.apache.jena.rdf.model.Model;

public interface RetentionPolicyCreator {
	RetentionPolicy createRetentionPolicy(Model model);
}
