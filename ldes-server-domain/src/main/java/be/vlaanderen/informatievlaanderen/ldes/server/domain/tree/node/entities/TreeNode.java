package be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.node.entities;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.TreeRelation;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.entities.Member;

import java.util.List;

public class TreeNode {
	private final String fragmentId;
	private final boolean immutable;
	private final boolean softDeleted;
	private final boolean isView;
	private final List<TreeRelation> relations;
	private final List<Member> members;

	public TreeNode(String fragmentId, boolean immutable, boolean softDeleted, boolean isView,
			List<TreeRelation> relations,
			List<Member> members) {
		this.fragmentId = fragmentId;
		this.immutable = immutable;
		this.softDeleted = softDeleted;
		this.isView = isView;
		this.relations = relations;
		this.members = members;
	}

	public String getFragmentId() {
		return fragmentId;
	}

	public boolean isImmutable() {
		return immutable;
	}

	public boolean isSoftDeleted() {
		return softDeleted;
	}

	public List<TreeRelation> getRelations() {
		return relations;
	}

	public List<Member> getMembers() {
		return members;
	}

	public boolean isView() {
		return isView;
	}
}
