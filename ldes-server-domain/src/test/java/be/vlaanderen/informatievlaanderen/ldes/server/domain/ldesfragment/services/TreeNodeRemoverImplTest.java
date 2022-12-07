package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.retentionpolicy.RetentionPolicy;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.retentionpolicy.timebased.TimeBasedRetentionPolicy;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.FragmentInfo;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.repository.MemberRepository;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.mockito.Mockito.*;

class TreeNodeRemoverImplTest {

	private final LdesFragmentRepository fragmentRepository = mock(LdesFragmentRepository.class);
	private final Map<String, List<RetentionPolicy>> retentionPolicyMap = Map.of("view",
			List.of(new TimeBasedRetentionPolicy("PT0S")));
	private final MemberRepository memberRepository = mock(MemberRepository.class);
	private final ParentUpdater parentUpdater = mock(ParentUpdater.class);
	private final TreeNodeRemover treeNodeRemover = new TreeNodeRemoverImpl(fragmentRepository, retentionPolicyMap,
			memberRepository, parentUpdater);

	@Test
	void when_NodeIsImmutableAndSatisfiesRetentionPoliciesOfView_NodeCanBeSoftDeleted() {
		LdesFragment notReadyToDeleteFragment = notReadyToDeleteFragment();
		LdesFragment readyToDeleteFragment = readyToDeleteFragment();
		when(fragmentRepository.retrieveNonDeletedImmutableFragmentsOfView("view"))
				.thenReturn(Stream.of(notReadyToDeleteFragment, readyToDeleteFragment));

		treeNodeRemover.removeTreeNodes();

		verify(fragmentRepository, times(1)).retrieveNonDeletedImmutableFragmentsOfView("view");
		verify(fragmentRepository, times(1)).setSoftDeleted(readyToDeleteFragment);
		verifyNoMoreInteractions(fragmentRepository);
		verify(parentUpdater, times(1)).updateParent(readyToDeleteFragment);
		verifyNoMoreInteractions(parentUpdater);
		verify(memberRepository, times(1)).removeMemberReference("memberId", "/view");
		verify(memberRepository, times(1)).removeMembersWithNoReferences();
		verifyNoMoreInteractions(memberRepository);
	}

	private LdesFragment notReadyToDeleteFragment() {
		return new LdesFragment(new FragmentInfo("view", List.of(), true, LocalDateTime.now().plusDays(1), false,
				numberOfMembers));
	}

	private LdesFragment readyToDeleteFragment() {
		LdesFragment ldesFragment = new LdesFragment(
				new FragmentInfo("view", List.of(), true, LocalDateTime.now(), false, numberOfMembers));
		ldesFragment.addMember("memberId");
		return ldesFragment;
	}

}