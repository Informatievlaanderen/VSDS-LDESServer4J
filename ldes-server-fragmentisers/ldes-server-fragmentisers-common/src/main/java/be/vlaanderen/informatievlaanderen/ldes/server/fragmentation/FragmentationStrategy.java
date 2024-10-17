package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation;

import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Bucket;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.FragmentationMember;
import io.micrometer.observation.Observation;

public interface FragmentationStrategy {
	void addMemberToBucket(Bucket rootBucketOfView, FragmentationMember member, Observation parentObservation);
}