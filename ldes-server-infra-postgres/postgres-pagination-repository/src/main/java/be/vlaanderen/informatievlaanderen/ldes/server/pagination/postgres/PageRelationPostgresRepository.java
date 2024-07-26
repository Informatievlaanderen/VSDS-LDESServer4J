package be.vlaanderen.informatievlaanderen.ldes.server.pagination.postgres;

import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.valueobjects.BucketRelation;
import be.vlaanderen.informatievlaanderen.ldes.server.pagination.postgres.repository.RelationEntityRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.pagination.repositories.PageRelationRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class PageRelationPostgresRepository implements PageRelationRepository {
	private final RelationEntityRepository relationEntityRepository;

	public PageRelationPostgresRepository(RelationEntityRepository relationEntityRepository) {
		this.relationEntityRepository = relationEntityRepository;
	}

	@Override
	@Transactional
	public void insertBucketRelation(BucketRelation bucketRelation) {
		relationEntityRepository.insertRelation(
				bucketRelation.fromBucket().createPartialUrl(),
				bucketRelation.toBucket().createPartialUrl(),
				bucketRelation.treeRelationType(),
				bucketRelation.treeValue(),
				bucketRelation.treeValueType(),
				bucketRelation.treePath()
		);
	}
}
