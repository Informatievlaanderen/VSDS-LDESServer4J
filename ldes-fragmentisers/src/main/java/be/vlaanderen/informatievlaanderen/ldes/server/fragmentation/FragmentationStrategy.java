package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import io.micrometer.observation.Observation;
import org.apache.jena.rdf.model.Model;

public interface FragmentationStrategy {
	void addMemberToFragment(LdesFragment rootFragmentOfView, String memberId, Model memberModel, Observation parentObservation);
}