package be.vlaanderen.informatievlaanderen.ldes.server.rest.treenode;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.MissingFragmentException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.LdesFragmentIdentifier;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Fragment;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.AllocationRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.FragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.repositories.MemberRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.rest.treenode.entities.TreeNode;
import be.vlaanderen.informatievlaanderen.ldes.server.rest.treenode.services.TreenodeUrlCreator;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class TreeNodeFactoryImpl implements TreeNodeFactory {

	private final FragmentRepository fragmentRepository;
	private final AllocationRepository allocationRepository;
	private final MemberRepository memberRepository;

	public TreeNodeFactoryImpl(FragmentRepository fragmentRepository, AllocationRepository allocationRepository,
			MemberRepository memberRepository) {
		this.fragmentRepository = fragmentRepository;
		this.allocationRepository = allocationRepository;
		this.memberRepository = memberRepository;
	}

	@Override
	public TreeNode getTreeNode(LdesFragmentIdentifier treeNodeId, String hostName, String collectionName) {
		String extendedTreeNodeId = TreenodeUrlCreator.encode(hostName, treeNodeId);
		Fragment fragment = fragmentRepository.retrieveFragment(treeNodeId)
				.orElseThrow(
						() -> new MissingFragmentException(extendedTreeNodeId));

		List<Member> members = allocationRepository.findMembersForFragment(treeNodeId.asString())
				.map(memberRepository::findById)
				.filter(Optional::isPresent)
				.map(Optional::get)
				.toList();
		return new TreeNode(extendedTreeNodeId, fragment.isImmutable(),
				fragment.getFragmentPairs().isEmpty(), fragment.getRelations(),
				members, collectionName);
	}

}
