package be.vlaanderen.informatievlaanderen.ldes.server.fetching.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.MissingFragmentException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.LdesFragmentIdentifier;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.TreeRelation;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.fetching.entities.MemberAllocation;
import be.vlaanderen.informatievlaanderen.ldes.server.fetching.entities.TreeNode;
import be.vlaanderen.informatievlaanderen.ldes.server.fetching.repository.AllocationRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Fragment;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.FragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.repositories.MemberRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TreeNodeFactoryImplTest {

	public static final String HOSTNAME = "http://localhost:8089";
	public static final String COLLECTION_NAME = "collectionName";
	public static final String VIEW = "treeNodeId";
	private static final ViewName VIEW_NAME = new ViewName(COLLECTION_NAME, VIEW);
	public static final LdesFragmentIdentifier TREE_NODE_ID = new LdesFragmentIdentifier(VIEW_NAME, List.of());

	private TreeNodeFactory treeNodeFactory;
	private FragmentRepository fragmentRepository;
	private AllocationRepository allocationRepository;
	private MemberRepository memberRepository;

	@BeforeEach
	void setUp() {
		fragmentRepository = mock(FragmentRepository.class);
		memberRepository = mock(MemberRepository.class);
		allocationRepository = mock(AllocationRepository.class);
		treeNodeFactory = new TreeNodeFactoryImpl(fragmentRepository, allocationRepository, memberRepository);
	}

	@Test
    void when_LdesFragmentDoesNotExist_ThrowMissingFragmentException() {
        when(fragmentRepository.retrieveFragment(TREE_NODE_ID)).thenReturn(Optional.empty());

        MissingFragmentException treeNodeId = Assertions.assertThrows(MissingFragmentException.class,
                () -> treeNodeFactory.getTreeNode(TREE_NODE_ID, HOSTNAME, COLLECTION_NAME));

        Assertions.assertEquals(
                "No fragment exists with fragment identifier: " + HOSTNAME + "/" + COLLECTION_NAME + "/treeNodeId",
                treeNodeId.getMessage());
    }

	@Test
	void when_LdesFragmentExists_ReturnTreeNode() {
		Fragment fragment = new Fragment(new LdesFragmentIdentifier(VIEW_NAME, List.of()));
		fragment.addRelation(new TreeRelation("path", LdesFragmentIdentifier.fromFragmentId("/col/view"), "value",
				"valueType", "relation"));
		when(fragmentRepository.retrieveFragment(TREE_NODE_ID)).thenReturn(Optional.of(fragment));
		Member member = new Member("member", COLLECTION_NAME, 0L, null);
		when(allocationRepository.getMemberAllocationsByFragmentId(TREE_NODE_ID.asString()))
				.thenReturn(List.of(new MemberAllocation("id", "", "", "", "member")));
		when(memberRepository.findAllByIds(List.of("member"))).thenReturn(List.of(member));

		TreeNode treeNode = treeNodeFactory.getTreeNode(TREE_NODE_ID, HOSTNAME, COLLECTION_NAME);

		Assertions.assertEquals(HOSTNAME + fragment.getFragmentIdString(), treeNode.getFragmentId());
		Assertions.assertEquals(fragment.isImmutable(), treeNode.isImmutable());
		Assertions.assertEquals(List.of(member), treeNode.getMembers());
		Assertions.assertEquals(
				List.of(new TreeRelation("path", LdesFragmentIdentifier.fromFragmentId("/col/view"), "value",
						"valueType",
						"relation")),
				treeNode.getRelations());
	}

}
