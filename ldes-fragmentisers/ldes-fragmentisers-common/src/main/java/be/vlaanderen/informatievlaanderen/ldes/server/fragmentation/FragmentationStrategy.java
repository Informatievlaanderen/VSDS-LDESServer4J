package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation;

import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Bucket;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.BucketisedMember;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.FragmentationMember;
import io.micrometer.observation.Observation;

import java.util.List;

public interface FragmentationStrategy {
	List<BucketisedMember> addMemberToBucketAndReturnMembers(Bucket rootFragmentOfView, FragmentationMember member, Observation parentObservation);
	void addMemberToBucket(Bucket rootBucketOfView, FragmentationMember member, Observation parentObservation);
}