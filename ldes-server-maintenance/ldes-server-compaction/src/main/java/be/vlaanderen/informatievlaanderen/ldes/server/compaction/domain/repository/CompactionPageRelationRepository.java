package be.vlaanderen.informatievlaanderen.ldes.server.compaction.domain.repository;

import java.util.List;

public interface CompactionPageRelationRepository {
	void updateCompactionBucketRelations(List<Long> compactedPageIds, long targetId);
}
