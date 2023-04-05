package be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.node.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.config.LdesConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.MissingFragmentException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.TreeRelation;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.repository.MemberRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.node.entities.TreeNode;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.relations.TreeRelationsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TreeNodeFactoryImplTest {

	public static final String HOSTNAME = "http://localhost:8089";
	public static final String COLLECTION_NAME = "collection";
	public static final String VIEW_NAME = "treeNodeId";
	public static final String TREE_NODE_ID = "/" + VIEW_NAME;

	private TreeNodeFactory treeNodeFactory;
	private LdesFragmentRepository ldesFragmentRepository;
	private MemberRepository memberRepository;
	private TreeRelationsRepository treeRelationsRepository;

	@BeforeEach
	void setUp() {
		ldesFragmentRepository = mock(LdesFragmentRepository.class);
		memberRepository = mock(MemberRepository.class);
		treeRelationsRepository = mock(TreeRelationsRepository.class);
		LdesConfig ldesConfig = new LdesConfig();
		ldesConfig.setHostName(HOSTNAME);
		ldesConfig.setCollectionName(COLLECTION_NAME);
		treeNodeFactory = new TreeNodeFactoryImpl(ldesConfig,
				ldesFragmentRepository, memberRepository, treeRelationsRepository);
	}

	@Test
	void when_LdesFragmentDoesNotExist_ThrowMissingFragmentException() {
		when(ldesFragmentRepository.retrieveFragment(TREE_NODE_ID)).thenReturn(Optional.empty());
		MissingFragmentException treeNodeId = assertThrows(MissingFragmentException.class,
				() -> treeNodeFactory.getTreeNode(TREE_NODE_ID));

		assertEquals("No fragment exists with fragment identifier: " + HOSTNAME + "/" + COLLECTION_NAME + "/treeNodeId",
				treeNodeId.getMessage());
	}

	@Test
	void when_LdesFragmentExists_ReturnTreeNode() {
		LdesFragment ldesFragment = new LdesFragment(VIEW_NAME, List.of());
		when(ldesFragmentRepository.retrieveFragment(TREE_NODE_ID)).thenReturn(Optional.of(ldesFragment));
		List<Member> members = List.of(new Member("member", null, List.of()));
		when(memberRepository.getMembersByReference(TREE_NODE_ID)).thenReturn(members);
		List<TreeRelation> treeRelations = List.of(new TreeRelation("path", "node", "value", "valueType", "relation"));
		when(treeRelationsRepository.getRelations(TREE_NODE_ID)).thenReturn(treeRelations);

		TreeNode treeNode = treeNodeFactory.getTreeNode(TREE_NODE_ID);

		assertEquals(ldesFragment.getFragmentId(), treeNode.getFragmentId());
		assertEquals(ldesFragment.isImmutable(), treeNode.isImmutable());
		assertEquals(ldesFragment.isImmutable(), treeNode.isSoftDeleted());
		assertEquals(members, treeNode.getMembers());
		assertEquals(treeRelations, treeNode.getRelations());
	}

}