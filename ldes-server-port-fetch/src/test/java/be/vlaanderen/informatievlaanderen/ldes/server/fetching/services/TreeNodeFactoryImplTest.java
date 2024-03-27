package be.vlaanderen.informatievlaanderen.ldes.server.fetching.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.MissingResourceException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.LdesFragmentIdentifier;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.TreeRelation;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.fetching.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.fetching.entities.MemberAllocation;
import be.vlaanderen.informatievlaanderen.ldes.server.fetching.entities.TreeNode;
import be.vlaanderen.informatievlaanderen.ldes.server.fetching.repository.AllocationRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Fragment;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.FragmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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
	private MemberFetcher memberFetcher;

	@BeforeEach
	void setUp() {
		fragmentRepository = mock(FragmentRepository.class);
		memberFetcher = mock(MemberFetcher.class);
		allocationRepository = mock(AllocationRepository.class);
		treeNodeFactory = new TreeNodeFactoryImpl(fragmentRepository, allocationRepository, memberFetcher);
	}

	@Test
	void when_LdesFragmentDoesNotExist_ThrowMissingFragmentException() {
		when(fragmentRepository.retrieveFragment(TREE_NODE_ID)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> treeNodeFactory.getTreeNode(TREE_NODE_ID, HOSTNAME, COLLECTION_NAME))
				.isInstanceOf(MissingResourceException.class)
				.hasMessage("Resource of type: fragment with id: /%s/%s could not be found.", COLLECTION_NAME, VIEW);
	}

	@Test
	void when_LdesFragmentExists_ReturnTreeNode() {
		Fragment fragment = new Fragment(new LdesFragmentIdentifier(VIEW_NAME, List.of()));
		fragment.addRelation(new TreeRelation("path", LdesFragmentIdentifier.fromFragmentId("/col/view"), "value",
				"valueType", "relation"));
		when(fragmentRepository.retrieveFragment(TREE_NODE_ID)).thenReturn(Optional.of(fragment));
		Member member = new Member("member", null);
		when(allocationRepository.getMemberAllocationsByFragmentId(TREE_NODE_ID.asDecodedFragmentId()))
				.thenReturn(List.of(new MemberAllocation("id", "", "", "", "member")));
		when(memberFetcher.fetchAllByIds(List.of("member"))).thenReturn(List.of(member));

		TreeNode treeNode = treeNodeFactory.getTreeNode(TREE_NODE_ID, HOSTNAME, COLLECTION_NAME);

		assertThat(treeNode.getFragmentId()).isEqualTo(HOSTNAME + fragment.getFragmentIdString());
		assertThat(treeNode.isImmutable()).isEqualTo(fragment.isImmutable());
		assertThat(treeNode.getMembers()).containsExactly(member);
		assertThat(treeNode.getRelations()).containsExactly(
				new TreeRelation("path", LdesFragmentIdentifier.fromFragmentId("/col/view"), "value",
						"valueType",
						"relation")
		);
	}

}
