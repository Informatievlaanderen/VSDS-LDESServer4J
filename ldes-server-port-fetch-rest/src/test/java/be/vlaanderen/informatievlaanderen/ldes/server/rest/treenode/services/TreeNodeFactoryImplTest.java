package be.vlaanderen.informatievlaanderen.ldes.server.rest.treenode.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.MissingFragmentException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.LdesFragmentIdentifier;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.TreeRelation;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.repository.MemberRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.node.entities.TreeNode;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.FragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.rest.treenode.TreeNodeFactory;
import be.vlaanderen.informatievlaanderen.ldes.server.rest.treenode.TreeNodeFactoryImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Optional;

class TreeNodeFactoryImplTest {

	public static final String HOSTNAME = "http://localhost:8089";
	public static final String COLLECTION_NAME = "collectionName";
	public static final String VIEW = "treeNodeId";
	private static final ViewName VIEW_NAME = new ViewName(COLLECTION_NAME, VIEW);
	public static final LdesFragmentIdentifier TREE_NODE_ID = new LdesFragmentIdentifier(VIEW_NAME, List.of());

	private TreeNodeFactory treeNodeFactory;
	private FragmentRepository fragmentRepository;
	private MemberRepository memberRepository;

	@BeforeEach
	void setUp() {
		fragmentRepository = Mockito.mock(FragmentRepository.class);
		memberRepository = Mockito.mock(MemberRepository.class);
		treeNodeFactory = new TreeNodeFactoryImpl(fragmentRepository, memberRepository);
	}

	@Test
	void when_LdesFragmentDoesNotExist_ThrowMissingFragmentException() {
		Mockito.when(fragmentRepository.retrieveFragment(TREE_NODE_ID)).thenReturn(Optional.empty());

		MissingFragmentException treeNodeId = Assertions.assertThrows(MissingFragmentException.class,
				() -> treeNodeFactory.getTreeNode(TREE_NODE_ID, HOSTNAME, COLLECTION_NAME));

		Assertions.assertEquals(
				"No fragment exists with fragment identifier: " + HOSTNAME + "/" + COLLECTION_NAME + "/treeNodeId",
				treeNodeId.getMessage());
	}

	@Test
	void when_LdesFragmentExists_ReturnTreeNode() {
		LdesFragment ldesFragment = new LdesFragment(new LdesFragmentIdentifier(VIEW_NAME, List.of()));
		ldesFragment.addRelation(new TreeRelation("path", LdesFragmentIdentifier.fromFragmentId("/col/view"), "value",
				"valueType", "relation"));
		Mockito.when(fragmentRepository.retrieveFragment(TREE_NODE_ID)).thenReturn(Optional.of(ldesFragment));
		List<Member> members = List.of(new Member("member", COLLECTION_NAME, 0L, null, null, null, List.of()));
		Mockito.when(memberRepository.getMembersByReference(TREE_NODE_ID.asString())).thenReturn(members.stream());

		TreeNode treeNode = treeNodeFactory.getTreeNode(TREE_NODE_ID, HOSTNAME, COLLECTION_NAME);

		Assertions.assertEquals(HOSTNAME + ldesFragment.getFragmentIdString(), treeNode.getFragmentId());
		Assertions.assertEquals(ldesFragment.isImmutable(), treeNode.isImmutable());
		Assertions.assertEquals(members, treeNode.getMembers());
		Assertions.assertEquals(
				List.of(new TreeRelation("path", LdesFragmentIdentifier.fromFragmentId("/col/view"), "value",
						"valueType",
						"relation")),
				treeNode.getRelations());
	}

}
