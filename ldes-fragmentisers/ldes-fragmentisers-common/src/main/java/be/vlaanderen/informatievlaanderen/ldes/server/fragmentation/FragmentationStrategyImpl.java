package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation;

import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.BucketisedMember;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Fragment;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Member;
import io.micrometer.observation.Observation;

import java.util.List;

public class FragmentationStrategyImpl implements FragmentationStrategy {

	@Override
	public List<BucketisedMember> addMemberToFragment(Fragment fragment, Member member,
													  Observation parentObservation) {
		return List.of(new BucketisedMember(member.id(), fragment.getViewName(), fragment.getFragmentIdString(), member.sequenceNr()));
	}
}
