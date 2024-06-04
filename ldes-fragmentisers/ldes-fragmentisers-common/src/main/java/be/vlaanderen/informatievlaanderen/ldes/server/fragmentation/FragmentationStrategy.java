package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation;

import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Fragment;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Member;
import io.micrometer.observation.Observation;

public interface FragmentationStrategy {
	void addMemberToBucket(Fragment rootFragmentOfView, Member member, Observation parentObservation);
	void saveBucket();
}