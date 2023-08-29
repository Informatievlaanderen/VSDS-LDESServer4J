package be.vlaanderen.informatievlaanderen.ldes.server.fetching.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.LdesFragmentIdentifier;
import be.vlaanderen.informatievlaanderen.ldes.server.fetching.entities.MemberAllocation;
import be.vlaanderen.informatievlaanderen.ldes.server.fetching.entities.TreeNode;
import be.vlaanderen.informatievlaanderen.ldes.server.fetching.exceptions.MissingFragmentException;
import be.vlaanderen.informatievlaanderen.ldes.server.fetching.repository.AllocationRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Fragment;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.FragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.repositories.MemberRepository;
import org.springframework.stereotype.Component;

import java.util.List;

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
		String extendedTreeNodeId = hostName + treeNodeId.asString();
		Fragment fragment = fragmentRepository.retrieveFragment(treeNodeId)
				.orElseThrow(
						() -> new MissingFragmentException(extendedTreeNodeId));

		List<MemberAllocation> memberIds = allocationRepository.getMemberAllocationsByFragmentId(treeNodeId.asString());
		List<Member> members = memberRepository
				.findAllByIds(memberIds.stream().map(MemberAllocation::getMemberId).toList());
		return new TreeNode(extendedTreeNodeId, fragment.isImmutable(),
				fragment.getFragmentPairs().isEmpty(), fragment.getRelations(),
				members, collectionName);
	}

}
