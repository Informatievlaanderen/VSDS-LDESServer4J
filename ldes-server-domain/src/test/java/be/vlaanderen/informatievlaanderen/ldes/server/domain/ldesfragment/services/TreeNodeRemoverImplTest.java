package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.retentionpolicy.RetentionPolicy;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.retentionpolicy.creation.RetentionPolicyCreator;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.retentionpolicy.creation.RetentionPolicyCreatorImpl;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.retentionpolicy.timebased.TimeBasedRetentionPolicy;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.repository.MemberRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.services.TreeMemberRemover;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.view.valueobject.ViewAddedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.view.valueobject.ViewDeletedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

class TreeNodeRemoverImplTest {

	private static final ViewName VIEW_NAME = new ViewName("collectionName", "view");

	private final LdesFragmentRepository fragmentRepository = mock(LdesFragmentRepository.class);
	private final MemberRepository memberRepository = mock(MemberRepository.class);
	private final Map<ViewName, List<RetentionPolicy>> retentionPolicyMap = new HashMap<>();
	private final TreeMemberRemover treeMemberRemover = mock(TreeMemberRemover.class);
	private final ParentUpdater parentUpdater = mock(ParentUpdater.class);
	private final RetentionPolicyCreator retentionPolicyCreator = new RetentionPolicyCreatorImpl();
	private TreeNodeRemoverImpl treeNodeRemover;

	@BeforeEach
	void setUp() {
		retentionPolicyMap.put(new ViewName("collectionName", "view"), List.of(new TimeBasedRetentionPolicy("PT0S")));
		treeNodeRemover = new TreeNodeRemoverImpl(fragmentRepository, memberRepository,
				retentionPolicyMap,
				treeMemberRemover, parentUpdater, retentionPolicyCreator);
	}

	@Test
	void when_NodeIsImmutableAndSatisfiesRetentionPoliciesOfView_NodeCanBeSoftDeleted() {
		LdesFragment notReadyToDeleteFragment = notReadyToDeleteFragment();
		LdesFragment readyToDeleteFragment = readyToDeleteFragment();
		when(fragmentRepository.retrieveNonDeletedImmutableFragmentsOfView("collectionName/view"))
				.thenReturn(Stream.of(notReadyToDeleteFragment, readyToDeleteFragment));
		when(memberRepository.getMembersByReference("/collectionName/view"))
				.thenReturn(Stream.of(new Member("memberId", "collectionName", 0L, null, null, null, List.of())));

		treeNodeRemover.removeTreeNodes();

		verify(fragmentRepository,
				times(1)).retrieveNonDeletedImmutableFragmentsOfView("collectionName/view");
		verify(fragmentRepository, times(1)).saveFragment(readyToDeleteFragment);
		assertTrue(readyToDeleteFragment.isSoftDeleted());
		verifyNoMoreInteractions(fragmentRepository);
		verify(parentUpdater, times(1)).updateParent(readyToDeleteFragment);
		verifyNoMoreInteractions(parentUpdater);
		verify(treeMemberRemover, times(1)).tryRemovingMember("memberId");
		verifyNoMoreInteractions(treeMemberRemover);
	}

	@Test
	void test_AddingAndDeletingViews() {
		ViewSpecification viewSpecification = new ViewSpecification(new ViewName("collection", "additonalView"),
				List.of(), List.of());

		assertFalse(retentionPolicyMap.containsKey(viewSpecification.getName()));
		treeNodeRemover.handleViewAddedEvent(new ViewAddedEvent(viewSpecification));

		assertTrue(retentionPolicyMap.containsKey(viewSpecification.getName()));
		treeNodeRemover.handleViewDeletedEvent(new ViewDeletedEvent(viewSpecification.getName()));
		assertFalse(retentionPolicyMap.containsKey(viewSpecification.getName()));
	}

	private LdesFragment notReadyToDeleteFragment() {
		return new LdesFragment(VIEW_NAME, List.of(), true,
				LocalDateTime.now().plusDays(1), false, 0, List.of());
	}

	private LdesFragment readyToDeleteFragment() {
		return new LdesFragment(
				VIEW_NAME, List.of(), true, LocalDateTime.now(), false, 0, List.of());
	}

}