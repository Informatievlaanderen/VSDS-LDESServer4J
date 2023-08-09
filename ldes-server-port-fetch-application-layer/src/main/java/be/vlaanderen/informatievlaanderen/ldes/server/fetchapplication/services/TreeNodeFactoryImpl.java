package be.vlaanderen.informatievlaanderen.ldes.server.fetchapplication.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.MissingFragmentException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.LdesFragmentIdentifier;
import be.vlaanderen.informatievlaanderen.ldes.server.fetchdomain.entities.MemberAllocation;
import be.vlaanderen.informatievlaanderen.ldes.server.fetchapplication.entities.TreeNodeDto;
import be.vlaanderen.informatievlaanderen.ldes.server.fetchdomain.repository.AllocationRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.fetchdomain.valueobjects.*;
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
		String eventStreamIdentifier = hostName + "/" + collectionName;
		String treeNodeIdentifier = hostName + treeNodeId.asString();
		Fragment fragment = fragmentRepository.retrieveFragment(treeNodeId)
				.orElseThrow(
						() -> new MissingFragmentException(treeNodeIdentifier));

		List<String> memberIds = allocationRepository.getMemberAllocationsByFragmentId(treeNodeId.asString()).stream()
				.map(MemberAllocation::getMemberId).toList();
		List<Member> members = memberRepository
				.findAllByIds(memberIds);
		TreeNodeInfo treeNodeInfo = new TreeNodeInfo(treeNodeIdentifier, getRelations(fragment, hostName));
		TreeMemberList treeMemberList = new TreeMemberList(eventStreamIdentifier, getMembers(members));
		TreeNode treeNode = new TreeNode(treeNodeInfo, treeMemberList);
		return new TreeNodeDto(treeNode,
				treeNodeIdentifier,
				fragment.getRelations().stream().map(
						be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.TreeRelation::treeNode)
						.map(x -> hostName + x.asString()).toList(),
				memberIds, fragment.isImmutable(), fragment.getFragmentPairs().isEmpty(),
				collectionName);
	}

	private static List<TreeMember> getMembers(List<Member> members) {
		return members.stream().map(member -> new TreeMember(member.getMemberIdWithoutPrefix(), member.getModel()))
				.toList();
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
