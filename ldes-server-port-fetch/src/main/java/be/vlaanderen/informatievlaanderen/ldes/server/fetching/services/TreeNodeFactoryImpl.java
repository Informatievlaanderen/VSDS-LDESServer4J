package be.vlaanderen.informatievlaanderen.ldes.server.fetching.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.MissingResourceException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.LdesFragmentIdentifier;
import be.vlaanderen.informatievlaanderen.ldes.server.fetching.entities.MemberAllocation;
import be.vlaanderen.informatievlaanderen.ldes.server.fetching.entities.TreeNode;
import be.vlaanderen.informatievlaanderen.ldes.server.fetching.repository.AllocationRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Fragment;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.FragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.repositories.MemberRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.ServerConfig.HOST_NAME_KEY;

@Component
public class TreeNodeFactoryImpl implements TreeNodeFactory {

	private final FragmentRepository fragmentRepository;
	private final AllocationRepository allocationRepository;
	private final MemberRepository memberRepository;

	public TreeNodeFactoryImpl(FragmentRepository fragmentRepository, AllocationRepository allocationRepository,
							   MemberRepository memberRepository, @Value(HOST_NAME_KEY) Boolean useRelativeUrl) {
		this.fragmentRepository = fragmentRepository;
		this.allocationRepository = allocationRepository;
		this.memberRepository = memberRepository;
	}

	@Override
	public TreeNode getTreeNode(LdesFragmentIdentifier treeNodeId, String hostName, String collectionName) {
		String extendedTreeNodeId = treeNodeId.asString();
		if (true){
			extendedTreeNodeId = hostName + extendedTreeNodeId;
		}
		Fragment fragment = fragmentRepository.retrieveFragment(treeNodeId)
				.orElseThrow(
						() -> new MissingResourceException("fragment", treeNodeId.asString()));

		List<MemberAllocation> memberIds = allocationRepository.getMemberAllocationsByFragmentId(treeNodeId.asString());
		List<Member> members = memberRepository
				.findAllByIds(memberIds.stream().map(MemberAllocation::getMemberId).toList());

		TreeNode treeNode = new TreeNode(extendedTreeNodeId, fragment.isImmutable(),
				fragment.getFragmentPairs().isEmpty(), fragment.getRelations(),
				members, collectionName);

		if(fragment.isRoot()) {
			// 04/12/23 Desactivated due to performance issues on the count query
			// refer to: https://github.com/Informatievlaanderen/VSDS-LDESServer4J/issues/1028
//			treeNode.setNumberOfMembersInView(allocationRepository.countByCollectionNameAndViewName(collectionName, treeNodeId.getViewName().getViewName()));
		}

		return treeNode;
	}

}
