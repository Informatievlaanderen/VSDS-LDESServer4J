package be.vlaanderen.informatievlaanderen.ldes.server.pagination.postgres.mapper;

import be.vlaanderen.informatievlaanderen.ldes.server.fetching.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.fetching.entities.TreeNode;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.postgres.projection.TreeMemberProjection;
import be.vlaanderen.informatievlaanderen.ldes.server.pagination.postgres.entity.PageEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.pagination.postgres.projection.TreeNodeProjection;
import be.vlaanderen.informatievlaanderen.ldes.server.pagination.postgres.projection.TreeRelationProjection;

import java.util.List;

public class TreeNodeMapper {
	private TreeNodeMapper() {
	}

	public static TreeNode fromPageEntity(PageEntity pageEntity, List<TreeMemberProjection> members) {
		return new TreeNode(
				pageEntity.getPartialUrl(),
				pageEntity.isImmutable(),
				pageEntity.isView(),
				pageEntity.getRelations().stream().map(TreeRelationMapper::fromRelationEntity).toList(),
				members.stream().map(memberEntity -> new Member(memberEntity.getSubject(), memberEntity.getModel())).toList(),
				pageEntity.getBucket().getView().getEventStream().getName(),
				pageEntity.getNextUpdateTs()
		);
	}

	public static TreeNode fromProjection(TreeNodeProjection projection, List<TreeRelationProjection> relations, List<TreeMemberProjection> members) {
		return new TreeNode(
				projection.getPartialUrl(),
				projection.isImmutable(),
				projection.isView(),
				relations.stream().map(TreeRelationMapper::fromProjection).toList(),
				members.stream().map(memberEntity -> new Member(memberEntity.getSubject(), memberEntity.getModel())).toList(),
				projection.getCollectionName(),
				projection.getNextUpdateTs()
		);
	}
}
