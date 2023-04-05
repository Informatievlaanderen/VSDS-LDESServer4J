package be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.node.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.config.LdesConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.MissingFragmentException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.TreeRelation;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.repository.MemberRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.node.entities.TreeNode;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.relations.TreeRelationsRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TreeNodeFactoryImpl implements TreeNodeFactory {
	private final LdesConfig ldesConfig;
	private final LdesFragmentRepository ldesFragmentRepository;
	private final MemberRepository memberRepository;
	private final TreeRelationsRepository treeRelationsRepository;

	public TreeNodeFactoryImpl(LdesConfig ldesConfig, LdesFragmentRepository ldesFragmentRepository,
			MemberRepository memberRepository, TreeRelationsRepository treeRelationsRepository) {
		this.ldesConfig = ldesConfig;
		this.ldesFragmentRepository = ldesFragmentRepository;
		this.memberRepository = memberRepository;
		this.treeRelationsRepository = treeRelationsRepository;
	}

	public TreeNode getTreeNode(String treeNodeId) {
		LdesFragment ldesFragment = ldesFragmentRepository.retrieveFragment(treeNodeId)
				.orElseThrow(
						() -> new MissingFragmentException(ldesConfig.getHostName() + treeNodeId));
		List<TreeRelation> relations = treeRelationsRepository.getRelations(treeNodeId);
		List<Member> members = memberRepository.getMembersByReference(treeNodeId);
		return new TreeNode(treeNodeId, ldesFragment.isImmutable(), ldesFragment.isSoftDeleted(), relations, members);
	}

}
