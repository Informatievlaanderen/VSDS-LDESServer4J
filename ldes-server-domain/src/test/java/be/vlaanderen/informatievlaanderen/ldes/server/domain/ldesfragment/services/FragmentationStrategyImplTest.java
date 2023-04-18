package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.repository.MemberRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.mockito.Mockito.*;

class FragmentationStrategyImplTest {
	private final LdesFragmentRepository ldesFragmentRepository = mock(LdesFragmentRepository.class);
	private final MemberRepository memberRepository = mock(MemberRepository.class);
	private final NonCriticalTasksExecutor nonCriticalTasksExecutor = mock(NonCriticalTasksExecutor.class);

	private final FragmentationStrategyImpl fragmentationStrategy = new FragmentationStrategyImpl(
			ldesFragmentRepository,
			memberRepository, nonCriticalTasksExecutor);

	@Test
	void when_memberIsAddedToFragment_FragmentationStrategyImplSavesUpdatedFragment() {
		LdesFragment ldesFragment = new LdesFragment(new ViewName("collectionName", "view"),
				List.of());
		Member member = mock(Member.class);
		when(member.getLdesMemberId()).thenReturn("memberId");

		fragmentationStrategy.addMemberToFragment(ldesFragment, member, any());

		verify(nonCriticalTasksExecutor, times(1)).submit(any(Runnable.class));
		verify(ldesFragmentRepository, times(1)).incrementNumberOfMembers(ldesFragment.getFragmentId());
	}
}