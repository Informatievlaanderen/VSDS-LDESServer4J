package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation;

import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.BucketisedMember;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Fragment;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Member;
import io.micrometer.observation.Observation;

public class FragmentationStrategyImpl implements FragmentationStrategy {
	private final BucketisedMemberSaver bucketisedMemberSaver;

	public FragmentationStrategyImpl(BucketisedMemberSaver bucketisedMemberSaver) {
		this.bucketisedMemberSaver = bucketisedMemberSaver;
	}

	@Override
	public void addMemberToBucket(Fragment fragment, Member member, Observation parentObservation) {
		bucketisedMemberSaver.addBucketisedMember(new BucketisedMember(member.id(), fragment.getViewName(), fragment.getFragmentIdString(), member.sequenceNr()));
	}

	@Override
	public void saveBucket() {
		bucketisedMemberSaver.flush();
	}
}
