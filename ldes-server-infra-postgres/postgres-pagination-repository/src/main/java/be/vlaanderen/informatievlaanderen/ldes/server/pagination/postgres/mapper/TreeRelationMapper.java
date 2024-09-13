package be.vlaanderen.informatievlaanderen.ldes.server.pagination.postgres.mapper;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.LdesFragmentIdentifier;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.TreeRelation;
import be.vlaanderen.informatievlaanderen.ldes.server.pagination.postgres.entity.PageRelationEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.pagination.postgres.projection.TreeRelationProjection;

public class TreeRelationMapper {
	private TreeRelationMapper() {}

	public static TreeRelation fromRelationEntity(PageRelationEntity pageRelationEntity) {
		return new TreeRelation(
				pageRelationEntity.getTreePath(),
				LdesFragmentIdentifier.fromFragmentId(pageRelationEntity.getToPage().getPartialUrl()),
				pageRelationEntity.getTreeValue(),
				pageRelationEntity.getTreeValueType(),
				pageRelationEntity.getTreeRelationType()
		);
	}

	public static TreeRelation fromProjection(TreeRelationProjection projection) {
		return new TreeRelation(
				projection.getTreePath(),
				LdesFragmentIdentifier.fromFragmentId(projection.getToPagePartialUrl()),
				projection.getTreeValue(),
				projection.getTreeValueType(),
				projection.getTreeRelationType()
		);
	}
}
