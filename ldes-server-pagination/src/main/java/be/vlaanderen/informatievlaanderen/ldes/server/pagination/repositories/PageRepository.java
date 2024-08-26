package be.vlaanderen.informatievlaanderen.ldes.server.pagination.repositories;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.fetching.entities.CompactionCandidate;
import be.vlaanderen.informatievlaanderen.ldes.server.pagination.entities.Page;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

public interface PageRepository {
	Page getOpenPage(long bucketId);
	int createPage(Long bucketId, String partialUrl);
	void setPageImmutable(long pageId);
	void setChildrenImmutableByBucketId(long bucketId);
    void markAllPagesImmutableByCollectionName(String collectionName);
    Stream<CompactionCandidate> getPossibleCompactionCandidates(ViewName viewName, int capacityPerPage);
    void deleteOutdatedFragments(LocalDateTime deleteTime);
    void setDeleteTime(List<Long> ids, LocalDateTime deleteTime);
}
