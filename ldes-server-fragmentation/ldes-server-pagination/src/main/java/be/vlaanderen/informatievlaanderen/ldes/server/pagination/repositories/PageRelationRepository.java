package be.vlaanderen.informatievlaanderen.ldes.server.pagination.repositories;

public interface PageRelationRepository {
	void insertGenericBucketRelation(long fromPageId, long toPageId);
}
