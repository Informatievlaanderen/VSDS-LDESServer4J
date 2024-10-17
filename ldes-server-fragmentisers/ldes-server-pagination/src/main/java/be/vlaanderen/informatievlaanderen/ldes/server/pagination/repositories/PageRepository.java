package be.vlaanderen.informatievlaanderen.ldes.server.pagination.repositories;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.fetching.entities.CompactionCandidate;
import be.vlaanderen.informatievlaanderen.ldes.server.pagination.entities.Page;

import java.time.LocalDateTime;
import java.util.List;

public interface PageRepository {
	Page getOpenPage(long bucketId);
	Page createNextPage(Page parentPage);
    void markAllPagesImmutableByCollectionName(String collectionName);
    List<CompactionCandidate> getPossibleCompactionCandidates(ViewName viewName, int capacityPerPage);
    void deleteOutdatedFragments(LocalDateTime deleteTime);
    void setDeleteTime(List<Long> ids, LocalDateTime deleteTime);
}
