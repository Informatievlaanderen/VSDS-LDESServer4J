package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.LdesFragmentIdentifier;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.AllocationRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.FragmentRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.mockito.Mockito.*;

class FragmentationStrategyImplTest {
	private final FragmentRepository fragmentRepository = mock(FragmentRepository.class);
	private final AllocationRepository allocationRepository = mock(AllocationRepository.class);
	private final NonCriticalTasksExecutor nonCriticalTasksExecutor = Mockito.mock(NonCriticalTasksExecutor.class);

	private final FragmentationStrategyImpl fragmentationStrategy = new FragmentationStrategyImpl(
			fragmentRepository,
			allocationRepository, nonCriticalTasksExecutor);

	@Test
	void when_memberIsAddedToFragment_FragmentationStrategyImplSavesUpdatedFragment() {
		LdesFragment ldesFragment = new LdesFragment(new LdesFragmentIdentifier(new ViewName("collectionName", "view"),
				List.of()));
		Member member = mock(Member.class);
		when(member.getLdesMemberId()).thenReturn("memberId");

		fragmentationStrategy.addMemberToFragment(ldesFragment, member.getLdesMemberId(), member.getModel(), any());

		verify(nonCriticalTasksExecutor, times(1)).submit(any(Runnable.class));
		verify(fragmentRepository, times(1)).incrementNumberOfMembers(ldesFragment.getFragmentId());
	}
}