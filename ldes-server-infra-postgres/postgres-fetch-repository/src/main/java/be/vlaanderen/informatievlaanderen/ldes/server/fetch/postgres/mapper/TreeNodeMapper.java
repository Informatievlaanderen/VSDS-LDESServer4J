package be.vlaanderen.informatievlaanderen.ldes.server.fetch.postgres.mapper;

import be.vlaanderen.informatievlaanderen.ldes.server.fetch.postgres.projection.TreeNodeProjection;
import be.vlaanderen.informatievlaanderen.ldes.server.fetching.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.fetching.entities.TreeNode;

import java.util.List;

public class TreeNodeMapper {
	private TreeNodeMapper() {
	}

	public static TreeNode fromProjection(TreeNodeProjection projection, List<Member> members) {
		return new TreeNode(
				projection.getPartialUrl(),
				projection.isImmutable(),
				projection.isView(),
				projection.getRelations().stream().map(TreeRelationMapper::fromRelation).toList(),
				members,
				projection.getBucket().getView().getEventStream().getName(),
				projection.getNextUpdateTs()
		);
	}

}
