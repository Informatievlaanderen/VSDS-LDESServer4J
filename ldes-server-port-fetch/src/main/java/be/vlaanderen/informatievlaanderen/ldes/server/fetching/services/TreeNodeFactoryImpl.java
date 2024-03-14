package be.vlaanderen.informatievlaanderen.ldes.server.fetching.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.MissingResourceException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.LdesFragmentIdentifier;
import be.vlaanderen.informatievlaanderen.ldes.server.fetching.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.fetching.entities.MemberAllocation;
import be.vlaanderen.informatievlaanderen.ldes.server.fetching.entities.TreeNode;
import be.vlaanderen.informatievlaanderen.ldes.server.fetching.repository.AllocationRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Fragment;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.FragmentRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TreeNodeFactoryImpl implements TreeNodeFactory {

	private final FragmentRepository fragmentRepository;
	private final AllocationRepository allocationRepository;
	private final MemberFetcher memberFetcher;

	public TreeNodeFactoryImpl(FragmentRepository fragmentRepository, AllocationRepository allocationRepository,
                               MemberFetcher memberFetcher) {
		this.fragmentRepository = fragmentRepository;
		this.allocationRepository = allocationRepository;
        this.memberFetcher = memberFetcher;
    }

	@Override
	public TreeNode getTreeNode(LdesFragmentIdentifier treeNodeId, String hostName, String collectionName) {
		String extendedTreeNodeId = hostName + treeNodeId.asEncodedFragmentId();
		Fragment fragment = fragmentRepository.retrieveFragment(treeNodeId)
				.orElseThrow(
						() -> new MissingResourceException("fragment", treeNodeId.asEncodedFragmentId()));

		List<MemberAllocation> memberIds = allocationRepository.getMemberAllocationsByFragmentId(treeNodeId.asDecodedFragmentId());
		List<Member> members = memberFetcher
				.fetchAllByIds(memberIds.stream().map(MemberAllocation::getMemberId).toList());

		TreeNode treeNode = new TreeNode(extendedTreeNodeId, fragment.isImmutable(),
				fragment.getFragmentPairs().isEmpty(), fragment.getRelations(),
				members, collectionName, fragment.getNextUpdateTs());

		if(fragment.isRoot()) {
			// 04/12/23 Desactivated due to performance issues on the count query
			// refer to: https://github.com/Informatievlaanderen/VSDS-LDESServer4J/issues/1028
//			treeNode.setNumberOfMembersInView(allocationRepository.countByCollectionNameAndViewName(collectionName, treeNodeId.getViewName().getViewName()));
		}

		return treeNode;
	}

}
