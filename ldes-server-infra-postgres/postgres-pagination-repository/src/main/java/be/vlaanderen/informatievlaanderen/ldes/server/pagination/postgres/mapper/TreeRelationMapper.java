package be.vlaanderen.informatievlaanderen.ldes.server.pagination.postgres.mapper;

import be.vlaanderen.informatievlaanderen.ldes.server.fetching.valueobjects.LdesFragmentIdentifier;
import be.vlaanderen.informatievlaanderen.ldes.server.fetching.valueobjects.TreeRelation;
import be.vlaanderen.informatievlaanderen.ldes.server.pagination.postgres.projection.TreeRelationProjection;

public class TreeRelationMapper {
	private TreeRelationMapper() {}

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
