package be.vlaanderen.informatievlaanderen.ldes.server.pagination.repositories;

import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.valueobjects.BucketRelation;

import java.util.List;

public interface PageRelationRepository {
	void insertBucketRelation(BucketRelation bucketRelation);

	void updateCompactionBucketRelations(List<Long> compactedPageIds, long targetId);
}
