package be.vlaanderen.informatievlaanderen.ldes.server.fetch.postgres.mapper;

import be.vlaanderen.informatievlaanderen.ldes.server.fetch.postgres.entity.FetchPageRelationEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.fetching.valueobjects.LdesFragmentIdentifier;
import be.vlaanderen.informatievlaanderen.ldes.server.fetching.valueobjects.TreeRelation;

public class TreeRelationMapper {
	private TreeRelationMapper() {}

	public static TreeRelation fromRelation(FetchPageRelationEntity pageRelation) {
		return new TreeRelation(
				pageRelation.getTreePath(),
				LdesFragmentIdentifier.fromFragmentId(pageRelation.getToPage().getPartialUrl()),
				pageRelation.getTreeValue(),
				pageRelation.getTreeValueType(),
				pageRelation.getTreeRelationType()
		);
	}
}
