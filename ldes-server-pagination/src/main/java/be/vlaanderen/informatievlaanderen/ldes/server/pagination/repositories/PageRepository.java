package be.vlaanderen.informatievlaanderen.ldes.server.pagination.repositories;

public interface PageRepository {
	void setChildrenImmutableByBucketId(long bucketId);
}
