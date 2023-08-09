package be.vlaanderen.informatievlaanderen.ldes.server.fetchapplication.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.MissingFragmentException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.LdesFragmentIdentifier;
import be.vlaanderen.informatievlaanderen.ldes.server.fetchdomain.entities.MemberAllocation;
import be.vlaanderen.informatievlaanderen.ldes.server.fetchapplication.entities.TreeNodeDto;
import be.vlaanderen.informatievlaanderen.ldes.server.fetchdomain.repository.AllocationRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.fetchdomain.valueobjects.TreeNode;
import be.vlaanderen.informatievlaanderen.ldes.server.fetchdomain.valueobjects.TreeNodeInfo;
import be.vlaanderen.informatievlaanderen.ldes.server.fetchdomain.valueobjects.TreeRelation;
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
	public TreeNodeDto getTreeNode(LdesFragmentIdentifier treeNodeId, String hostName, String collectionName) {
		String extendedTreeNodeId = hostName + treeNodeId.asString();
		Fragment fragment = fragmentRepository.retrieveFragment(treeNodeId)
				.orElseThrow(
						() -> new MissingFragmentException(extendedTreeNodeId));

		List<MemberAllocation> memberIds = allocationRepository.getMemberAllocationsByFragmentId(treeNodeId.asString());
		List<Member> members = memberRepository
				.findAllByIds(memberIds.stream().map(MemberAllocation::getMemberId).toList());
		return new TreeNodeDto(new TreeNode(new TreeNodeInfo(extendedTreeNodeId, getRelations(fragment, hostName))),
				extendedTreeNodeId,
				fragment.getRelations().stream().map(
						be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.TreeRelation::treeNode)
						.map(x -> hostName + x.asString()).toList(),
				fragment.isImmutable(), fragment.getFragmentPairs().isEmpty(),
				members, collectionName);
	}

	private List<TreeRelation> getRelations(Fragment fragment, String hostName) {
		return fragment.getRelations().stream().map(treeRelation -> getTreeRelation(hostName, treeRelation)).toList();
	}

	private TreeRelation getTreeRelation(String hostName,
			be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.TreeRelation treeRelation) {
		return new TreeRelation(treeRelation.treePath(), hostName + treeRelation.treeNode().asString(),
				treeRelation.treeValue(), treeRelation.treeValueType(), treeRelation.relation());
	}

}
