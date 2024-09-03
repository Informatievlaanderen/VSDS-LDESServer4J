package be.vlaanderen.informatievlaanderen.ldes.server.pagination.postgres.mapper;

import be.vlaanderen.informatievlaanderen.ldes.server.fetching.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.fetching.entities.TreeNode;
import be.vlaanderen.informatievlaanderen.ldes.server.pagination.postgres.projection.TreeNodeProjection;
import be.vlaanderen.informatievlaanderen.ldes.server.pagination.postgres.projection.TreeRelationProjection;

import java.util.List;

public class TreeNodeMapper {
	private TreeNodeMapper() {
	}

	public static TreeNode fromProjection(TreeNodeProjection projection, List<TreeRelationProjection> relations, List<Member> members) {
		return new TreeNode(
				projection.getPartialUrl(),
				projection.isImmutable(),
				projection.isView(),
				relations.stream().map(TreeRelationMapper::fromProjection).toList(),
				members,
				projection.getCollectionName(),
				projection.getNextUpdateTs()
		);
	}
}
