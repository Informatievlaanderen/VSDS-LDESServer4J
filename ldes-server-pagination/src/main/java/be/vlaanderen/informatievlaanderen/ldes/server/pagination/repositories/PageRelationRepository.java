package be.vlaanderen.informatievlaanderen.ldes.server.pagination.repositories;

import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.valueobjects.ExtendedBucketRelation;

import java.util.List;

public interface PageRelationRepository {
	void insertGenericBucketRelation(long fromPageId, long toPageId);
	void insertBucketRelation(ExtendedBucketRelation bucketRelation);

	void updateCompactionBucketRelations(List<Long> compactedPageIds, long targetId);
}
