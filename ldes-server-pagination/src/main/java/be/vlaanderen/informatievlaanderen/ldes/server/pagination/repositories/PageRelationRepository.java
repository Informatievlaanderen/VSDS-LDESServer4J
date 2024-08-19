package be.vlaanderen.informatievlaanderen.ldes.server.pagination.repositories;

import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.valueobjects.BucketRelation;

import java.util.List;

public interface PageRelationRepository {
	void insertGenericBucketRelation(long fromPageId, long toPageId);
	void insertBucketRelation(BucketRelation bucketRelation);

	void updateCompactionBucketRelations(List<Long> compactedPageIds, long targetId);
}
