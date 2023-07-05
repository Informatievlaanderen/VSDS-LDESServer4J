package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.retentionpolicy.timebased.TimeBasedRetentionPolicy;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.LdesFragmentIdentifier;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.valueobjects.FragmentPair;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.repository.MemberRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.services.TreeMemberRemover;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.mockito.Mockito.*;

class TreeNodeRemoverImplTest {

	private static final ViewName VIEW_NAME = new ViewName("collectionName", "view");

	private final LdesFragmentRepository fragmentRepository = mock(LdesFragmentRepository.class);
	private final MemberRepository memberRepository = mock(MemberRepository.class);
	private final TreeMemberRemover treeMemberRemover = mock(TreeMemberRemover.class);
	private final RetentionPolicyCollection retentionPolicyCollection = mock(RetentionPolicyCollection.class);
	private TreeNodeRemoverImpl treeNodeRemover;

	@BeforeEach
	void setUp() {
		treeNodeRemover = new TreeNodeRemoverImpl(fragmentRepository, memberRepository,
				treeMemberRemover, retentionPolicyCollection);
	}

	@Test
	void when_MembersOfFragmentMatchRetentionPoliciesOfView_MembersAreDeleted() {
		when(retentionPolicyCollection.getRetentionPolicyMap()).thenReturn(Map.of(new ViewName("collectionName", "view"), List.of(new TimeBasedRetentionPolicy(Duration.ZERO))));
		LdesFragment firstLdesFragmentOfView = ldesFragment("1");
		LdesFragment secondLdesFragmentOfView = ldesFragment("2");
		when(fragmentRepository.retrieveFragmentsOfView(VIEW_NAME.asString()))
				.thenReturn(Stream.of(firstLdesFragmentOfView, secondLdesFragmentOfView));
		Member firstMember = getMember("1");
		Member secondMember = getMember("2");
		when(memberRepository.getMembersByReference(firstLdesFragmentOfView.getFragmentIdString()))
				.thenReturn(Stream.of(firstMember, secondMember));
		Member thirdMember = getMember("3");
		when(memberRepository.getMembersByReference(secondLdesFragmentOfView.getFragmentIdString()))
				.thenReturn(Stream.of(thirdMember));

		treeNodeRemover.removeTreeNodeMembers();

		InOrder inOrder = inOrder(fragmentRepository, memberRepository, treeMemberRemover);
		inOrder.verify(fragmentRepository).retrieveFragmentsOfView(VIEW_NAME.asString());
		inOrder.verify(memberRepository).getMembersByReference(firstLdesFragmentOfView.getFragmentIdString());
		inOrder.verify(memberRepository).removeMemberReference(firstMember.getLdesMemberId(),
				firstLdesFragmentOfView.getFragmentIdString());
		inOrder.verify(treeMemberRemover).deletingMemberFromCollection(firstMember.getLdesMemberId());
		inOrder.verify(memberRepository).removeMemberReference(secondMember.getLdesMemberId(),
				firstLdesFragmentOfView.getFragmentIdString());
		inOrder.verify(treeMemberRemover).deletingMemberFromCollection(secondMember.getLdesMemberId());
		inOrder.verify(memberRepository).getMembersByReference(secondLdesFragmentOfView.getFragmentIdString());
		inOrder.verify(memberRepository).removeMemberReference(thirdMember.getLdesMemberId(),
				secondLdesFragmentOfView.getFragmentIdString());
		inOrder.verify(treeMemberRemover).deletingMemberFromCollection(thirdMember.getLdesMemberId());
		inOrder.verifyNoMoreInteractions();
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

	private Member getMember(String memberId) {
		return new Member(memberId, null, null, null, LocalDateTime.now(), null, null);
	}

	private LdesFragment ldesFragment(String page) {
		return new LdesFragment(new LdesFragmentIdentifier(VIEW_NAME, List.of(new FragmentPair("page", page))));
	}

}
