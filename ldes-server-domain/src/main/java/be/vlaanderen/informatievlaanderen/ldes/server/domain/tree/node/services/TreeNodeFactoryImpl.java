package be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.node.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.MissingFragmentException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.repository.MemberRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.node.entities.TreeNode;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.LdesConfig;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TreeNodeFactoryImpl implements TreeNodeFactory {

	private final LdesFragmentRepository ldesFragmentRepository;
	private final MemberRepository memberRepository;

	public TreeNodeFactoryImpl(LdesFragmentRepository ldesFragmentRepository, MemberRepository memberRepository) {
		this.ldesFragmentRepository = ldesFragmentRepository;
		this.memberRepository = memberRepository;
	}

	public TreeNode getTreeNode(String treeNodeId, LdesConfig ldesConfig) {
		String extendedTreeNodeId = ldesConfig.getHostName() + treeNodeId;
		LdesFragment ldesFragment = ldesFragmentRepository.retrieveFragment(treeNodeId)
				.orElseThrow(
						() -> new MissingFragmentException(extendedTreeNodeId));
		List<Member> members = memberRepository.getMembersByReference(treeNodeId).toList();
		return new TreeNode(extendedTreeNodeId, ldesFragment.isImmutable(), ldesFragment.isSoftDeleted(),
				ldesFragment.getFragmentPairs().isEmpty(), ldesFragment.getRelations(),
				members, ldesConfig.getCollectionName());
	}

}
