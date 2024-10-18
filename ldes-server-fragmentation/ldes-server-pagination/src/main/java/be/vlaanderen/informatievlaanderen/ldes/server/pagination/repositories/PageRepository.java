package be.vlaanderen.informatievlaanderen.ldes.server.pagination.repositories;

import be.vlaanderen.informatievlaanderen.ldes.server.pagination.entities.Page;

public interface PageRepository {
	Page getOpenPage(long bucketId);
	Page createNextPage(Page parentPage);
    void markAllPagesImmutableByCollectionName(String collectionName);
}
