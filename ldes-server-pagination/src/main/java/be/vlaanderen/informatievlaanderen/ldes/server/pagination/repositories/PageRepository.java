package be.vlaanderen.informatievlaanderen.ldes.server.pagination.repositories;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.fetching.entities.CompactionCandidate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

public interface PageRepository {
	void setChildrenImmutableByBucketId(long bucketId);

    void markAllPagesImmutableByCollectionName(String collectionName);

    Stream<CompactionCandidate> getPossibleCompactionCandidates(ViewName viewName, int capacityPerPage);

    void deleteOutdatedFragments(LocalDateTime deleteTime);

    void setDeleteTime(List<Long> ids, LocalDateTime deleteTime);
}
