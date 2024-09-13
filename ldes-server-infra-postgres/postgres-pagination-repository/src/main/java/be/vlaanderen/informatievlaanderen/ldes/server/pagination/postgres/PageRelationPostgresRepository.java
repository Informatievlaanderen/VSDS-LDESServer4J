package be.vlaanderen.informatievlaanderen.ldes.server.pagination.postgres;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.valueobjects.BucketRelation;
import be.vlaanderen.informatievlaanderen.ldes.server.pagination.postgres.repository.PageRelationEntityRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.pagination.repositories.PageRelationRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class PageRelationPostgresRepository implements PageRelationRepository {
	private final PageRelationEntityRepository pageRelationEntityRepository;

	public PageRelationPostgresRepository(PageRelationEntityRepository pageRelationEntityRepository) {
		this.pageRelationEntityRepository = pageRelationEntityRepository;
	}

	@Override
	@Transactional
	public void insertGenericBucketRelation(long fromPageId, long toPageId) {
		pageRelationEntityRepository.insertRelation(fromPageId, toPageId, RdfConstants.GENERIC_TREE_RELATION);
	}

	@Override
	@Transactional
	public void insertBucketRelation(BucketRelation bucketRelation) {
		pageRelationEntityRepository.insertRelation(
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
		pageRelationEntityRepository.updateToPageRelations(compactedPageIds, targetId);
	}
}
