package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation;

import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Bucket;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.BucketisedMember;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.FragmentationMember;
import io.micrometer.observation.Observation;

import java.util.List;

public class FragmentationStrategyImpl implements FragmentationStrategy {
	@Override
	public List<BucketisedMember> addMemberToBucketAndReturnMembers(Bucket bucket, FragmentationMember member, Observation parentObservation) {
		return List.of(new BucketisedMember(bucket.getBucketId(), member.getMemberId()));
	}

	@Override
	public Bucket addMemberToBucket(Bucket rootBucketOfView, FragmentationMember member, Observation parentObservation) {
		rootBucketOfView.addMember(member.getMemberId());
		return rootBucketOfView;
	}
}
