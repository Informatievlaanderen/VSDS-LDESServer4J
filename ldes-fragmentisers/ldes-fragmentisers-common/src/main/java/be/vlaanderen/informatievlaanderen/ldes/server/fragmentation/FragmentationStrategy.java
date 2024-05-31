package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation;

import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.BucketisedMember;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Fragment;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Member;
import io.micrometer.observation.Observation;

import java.util.List;

public interface FragmentationStrategy {
	List<BucketisedMember> addMemberToFragment(Fragment rootFragmentOfView, Member member, Observation parentObservation);
}