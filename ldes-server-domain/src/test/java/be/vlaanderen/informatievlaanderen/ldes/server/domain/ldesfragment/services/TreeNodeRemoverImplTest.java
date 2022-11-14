package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.retentionpolicy.RetentionPolicy;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.retentionpolicy.timebased.TimeBasedRetentionPolicy;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.FragmentInfo;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.services.TreeMemberRemover;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.memberreferences.entities.MemberReferencesRepository;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class TreeNodeRemoverImplTest {

	private final LdesFragmentRepository fragmentRepository = mock(LdesFragmentRepository.class);
	private final Map<String, List<RetentionPolicy>> retentionPolicyMap = Map.of("view",
			List.of(new TimeBasedRetentionPolicy(0)));
	private final MemberReferencesRepository referencesRepository = mock(MemberReferencesRepository.class);
	private final TreeMemberRemover treeMemberRemover = mock(TreeMemberRemover.class);
	private final ParentUpdater parentUpdater = mock(ParentUpdater.class);
	private final TreeNodeRemover treeNodeRemover = new TreeNodeRemoverImpl(fragmentRepository, retentionPolicyMap,
			referencesRepository, treeMemberRemover, parentUpdater);

	@Test
	void when_NodeIsImmutableAndSatisfiesRetentionPoliciesOfView_NodeCanBeSoftDeleted() {
		LdesFragment notReadyToDeleteFragment = notReadyToDeleteFragment();
		LdesFragment readyToDeleteFragment = readyToDeleteFragment();
		when(fragmentRepository.retrieveImmutableFragmentsOfView("view"))
				.thenReturn(Stream.of(notReadyToDeleteFragment, readyToDeleteFragment));

		treeNodeRemover.removeTreeNodes();

		verify(fragmentRepository, times(1)).retrieveImmutableFragmentsOfView("view");
		verify(fragmentRepository, times(1)).saveFragment(readyToDeleteFragment);
		assertTrue(readyToDeleteFragment.getFragmentInfo().getSoftDeleted());
		verifyNoMoreInteractions(fragmentRepository);
		verify(parentUpdater, times(1)).updateParent(readyToDeleteFragment);
		verifyNoMoreInteractions(parentUpdater);
		verify(referencesRepository, times(1)).removeMemberReference("memberId", "/view");
		verifyNoMoreInteractions(referencesRepository);
		verify(treeMemberRemover, times(1)).tryRemovingMember("memberId");
		verifyNoMoreInteractions(treeMemberRemover);
	}

	private LdesFragment notReadyToDeleteFragment() {
		return new LdesFragment(new FragmentInfo("view", List.of(), true, LocalDateTime.now().plusDays(1), false));
	}

	private LdesFragment readyToDeleteFragment() {
		LdesFragment ldesFragment = new LdesFragment(
				new FragmentInfo("view", List.of(), true, LocalDateTime.now(), false));
		ldesFragment.addMember("memberId");
		return ldesFragment;
	}

}