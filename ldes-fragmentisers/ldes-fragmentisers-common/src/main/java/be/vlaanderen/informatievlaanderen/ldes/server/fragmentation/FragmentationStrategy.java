package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation;

import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Fragment;
import io.micrometer.observation.Observation;
import org.apache.jena.rdf.model.Model;

public interface FragmentationStrategy {
	void addMemberToFragment(Fragment rootFragmentOfView, String memberId, Model memberModel, Observation parentObservation);
}