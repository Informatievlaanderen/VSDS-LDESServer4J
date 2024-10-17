package be.vlaanderen.informatievlaanderen.ldes.server.pagination.repositories;

import java.util.List;

public interface PageRelationRepository {
	void insertGenericBucketRelation(long fromPageId, long toPageId);

	void updateCompactionBucketRelations(List<Long> compactedPageIds, long targetId);
}
