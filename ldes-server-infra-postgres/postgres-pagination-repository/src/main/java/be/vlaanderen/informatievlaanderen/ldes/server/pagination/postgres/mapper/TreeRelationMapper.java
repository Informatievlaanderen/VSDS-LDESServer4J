package be.vlaanderen.informatievlaanderen.ldes.server.pagination.postgres.mapper;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.LdesFragmentIdentifier;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.TreeRelation;
import be.vlaanderen.informatievlaanderen.ldes.server.pagination.postgres.entity.RelationEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.pagination.postgres.projection.TreeRelationProjection;

public class TreeRelationMapper {
	private TreeRelationMapper() {}

	public static TreeRelation fromRelationEntity(RelationEntity relationEntity) {
		return new TreeRelation(
				relationEntity.getTreePath(),
				LdesFragmentIdentifier.fromFragmentId(relationEntity.getToPage().getPartialUrl()),
				relationEntity.getTreeValue(),
				relationEntity.getTreeValueType(),
				relationEntity.getTreeRelationType()
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
