package be.vlaanderen.informatievlaanderen.ldes.server.pagination.postgres;

import be.vlaanderen.informatievlaanderen.ldes.server.pagination.entities.PageRelation;
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
	public void insertPageRelation(PageRelation pageRelation) {
		relationEntityRepository.insertRelation(pageRelation.fromPage().partialUrl(), pageRelation.toPage().partialUrl(), pageRelation.treeRelationType(), pageRelation.treeValue(), pageRelation.treeValueType(), pageRelation.treePath());
	}
}
