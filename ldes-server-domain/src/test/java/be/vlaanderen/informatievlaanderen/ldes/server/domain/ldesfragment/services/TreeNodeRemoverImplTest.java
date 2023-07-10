package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.retention.MemberDeletedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.retention.MemberUnallocatedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.repository.MemberRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.services.TreeMemberRemover;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

import static org.mockito.Mockito.*;

class TreeNodeRemoverImplTest {

	private final LdesFragmentRepository fragmentRepository = mock(LdesFragmentRepository.class);
	private final MemberRepository memberRepository = mock(MemberRepository.class);
	private final TreeMemberRemover treeMemberRemover = mock(TreeMemberRemover.class);
	private TreeNodeRemoverImpl treeNodeRemover;

	@BeforeEach
	void setUp() {
		treeNodeRemover = new TreeNodeRemoverImpl(fragmentRepository, memberRepository, treeMemberRemover);
	}

	@Test
	void when_LdesFragmentOfViewAreRemoved_TheyAreRemovedFromRepository() {
		ViewName viewName = new ViewName("collection", "view");

		treeNodeRemover.removeLdesFragmentsOfView(viewName);

		InOrder inOrder = inOrder(memberRepository, fragmentRepository);
		inOrder.verify(memberRepository).removeViewReferences(viewName);
		inOrder.verify(fragmentRepository).removeLdesFragmentsOfView(viewName.asString());
		inOrder.verifyNoMoreInteractions();
	}

	@Test
	void should_DeleteTreeNodesByCollection_when_DeleteTreeNodesByCollectionIsCalled() {
		String collectionName = "collectionName";

		treeNodeRemover.deleteTreeNodesByCollection(collectionName);

		verify(fragmentRepository).deleteTreeNodesByCollection(collectionName);
	}

	@Test
	void when_MemberisUnallocated_then_ReferenceIsDeleted() {
		ViewName viewName = new ViewName("collection", "view");
		MemberUnallocatedEvent memberUnallocatedEvent = new MemberUnallocatedEvent("id", viewName);

		treeNodeRemover.handleMemberUnallocatedEvent(memberUnallocatedEvent);

		InOrder inOrder = inOrder(memberRepository, fragmentRepository, treeMemberRemover);
		inOrder.verify(memberRepository).removeViewReferenceOfMember("id", viewName);
		inOrder.verify(treeMemberRemover).deletingMemberFromCollection("id", "collection");
		inOrder.verifyNoMoreInteractions();
	}

	@Test
	void when_MemberIsDeletedEvent_then_MemberIsDeleted() {
		MemberDeletedEvent memberDeletedEvent = new MemberDeletedEvent("id");

		treeNodeRemover.handleMemberDeletedEvent(memberDeletedEvent);

		InOrder inOrder = inOrder(treeMemberRemover);
		inOrder.verify(treeMemberRemover).deletingMemberFromCollection("id", "collection");
		inOrder.verifyNoMoreInteractions();
	}

}
