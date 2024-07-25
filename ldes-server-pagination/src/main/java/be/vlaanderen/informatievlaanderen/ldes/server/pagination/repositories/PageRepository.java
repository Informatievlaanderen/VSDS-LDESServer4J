package be.vlaanderen.informatievlaanderen.ldes.server.pagination.repositories;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.fetching.entities.CompactionCandidate;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.stream.Stream;

public interface PageRepository {
	void setChildrenImmutableByBucketId(long bucketId);

	void markAllPagesImmutableByCollectionName(String collectionName);

    @Transactional
    Stream<CompactionCandidate> getPossibleCompactionCandidates(ViewName viewName, int capacityPerPage);

    @Transactional
    void deleteOutdatedFragments(LocalDateTime deleteTime);
}
