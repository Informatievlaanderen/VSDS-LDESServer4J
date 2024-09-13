package be.vlaanderen.informatievlaanderen.ldes.server.pagination.postgres;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.valueobjects.ExtendedBucketRelation;
import be.vlaanderen.informatievlaanderen.ldes.server.pagination.postgres.repository.RelationEntityRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.pagination.repositories.PageRelationRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class PageRelationPostgresRepository implements PageRelationRepository {
	private final RelationEntityRepository relationEntityRepository;

	public PageRelationPostgresRepository(RelationEntityRepository relationEntityRepository) {
		this.relationEntityRepository = relationEntityRepository;
	}

	@Override
	@Transactional
	public void insertGenericBucketRelation(long fromPageId, long toPageId) {
		relationEntityRepository.insertRelation(fromPageId, toPageId, RdfConstants.GENERIC_TREE_RELATION);
	}

	@Override
	@Transactional
	public void insertBucketRelation(ExtendedBucketRelation bucketRelation) {
		relationEntityRepository.insertRelation(
				bucketRelation.fromBucket().createPartialUrl(),
				bucketRelation.toBucket().createPartialUrl(),
				bucketRelation.treeRelationType(),
				bucketRelation.treeValue(),
				bucketRelation.treeValueType(),
				bucketRelation.treePath()
		);
	}

	@Override
	@Transactional
	public void updateCompactionBucketRelations(List<Long> compactedPageIds, long targetId) {
		relationEntityRepository.updateToPageRelations(compactedPageIds, targetId);
	}
}
