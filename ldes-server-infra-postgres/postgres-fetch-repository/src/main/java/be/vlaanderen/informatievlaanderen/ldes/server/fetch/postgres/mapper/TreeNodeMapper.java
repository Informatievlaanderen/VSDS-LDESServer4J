package be.vlaanderen.informatievlaanderen.ldes.server.fetch.postgres.mapper;

import be.vlaanderen.informatievlaanderen.ldes.server.fetch.postgres.projection.TreeNodeProjection;
import be.vlaanderen.informatievlaanderen.ldes.server.fetching.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.fetching.entities.TreeNode;

import java.util.List;

public class TreeNodeMapper {
	private TreeNodeMapper() {
	}

	public static TreeNode fromProjection(TreeNodeProjection page, List<Member> members) {
		return new TreeNode(
				page.getPartialUrl(),
				page.isImmutable(),
				page.isView(),
				page.getRelations().stream().map(TreeRelationMapper::fromRelation).toList(),
				members,
				page.getBucket().getView().getEventStream().getName(),
				page.getNextUpdateTs()
		);
	}

}
