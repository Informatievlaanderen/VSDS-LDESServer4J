package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.retentionpolicy.creation;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.retentionpolicy.RetentionPolicy;
import org.apache.jena.rdf.model.Model;

public class VersionBasedRetentionPolicyCreator implements RetentionPolicyCreator {
	@Override
	public RetentionPolicy createRetentionPolicy(Model model) {
		// TODO VSDSPUB-713
		return null;
	}
}