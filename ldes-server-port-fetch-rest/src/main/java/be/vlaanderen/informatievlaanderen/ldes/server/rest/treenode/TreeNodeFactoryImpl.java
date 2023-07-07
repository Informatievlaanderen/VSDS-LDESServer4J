package be.vlaanderen.informatievlaanderen.ldes.server.rest.treenode;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.MissingFragmentException;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Fragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.LdesFragmentIdentifier;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.repository.MemberRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.node.entities.TreeNode;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.node.services.TreenodeUrlCreator;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.FragmentRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TreeNodeFactoryImpl implements TreeNodeFactory {

	private final FragmentRepository fragmentRepository;
	private final MemberRepository memberRepository;

	public TreeNodeFactoryImpl(FragmentRepository fragmentRepository, MemberRepository memberRepository) {
		this.fragmentRepository = fragmentRepository;
		this.memberRepository = memberRepository;
	}

	@Override
	public TreeNode getTreeNode(LdesFragmentIdentifier treeNodeId, String hostName, String collectionName) {
		String extendedTreeNodeId = TreenodeUrlCreator.encode(hostName, treeNodeId);
		Fragment fragment = fragmentRepository.retrieveFragment(treeNodeId)
				.orElseThrow(
						() -> new MissingFragmentException(extendedTreeNodeId));
		List<Member> members = memberRepository.getMembersByReference(treeNodeId.asString()).toList();
		return new TreeNode(extendedTreeNodeId, fragment.isImmutable(),
				fragment.getFragmentPairs().isEmpty(), fragment.getRelations(),
				members, collectionName);
	}

}
