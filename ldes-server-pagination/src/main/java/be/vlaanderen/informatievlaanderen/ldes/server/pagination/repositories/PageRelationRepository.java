package be.vlaanderen.informatievlaanderen.ldes.server.pagination.repositories;

import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.valueobjects.BucketRelation;

public interface PageRelationRepository {
	void insertBucketRelation(BucketRelation bucketRelation);
}
