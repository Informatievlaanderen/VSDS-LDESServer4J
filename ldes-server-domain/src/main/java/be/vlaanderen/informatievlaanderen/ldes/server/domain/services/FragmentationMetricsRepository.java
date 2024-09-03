package be.vlaanderen.informatievlaanderen.ldes.server.domain.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.FragmentationMetric;

import java.util.List;

public interface FragmentationMetricsRepository {
	List<FragmentationMetric> getBucketisedMemberCounts(String collectionName);
	List<FragmentationMetric> getPaginatedMemberCounts(String collectionName);
}
