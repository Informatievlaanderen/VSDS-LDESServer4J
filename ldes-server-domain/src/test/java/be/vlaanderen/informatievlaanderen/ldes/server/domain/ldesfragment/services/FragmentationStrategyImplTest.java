package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.FragmentInfo;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.memberreferences.entities.MemberReferencesRepository;
import org.junit.jupiter.api.Test;
import org.springframework.cloud.sleuth.Tracer;

import java.util.List;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.TracerMockHelper.mockTracer;
import static org.mockito.Mockito.*;

class FragmentationStrategyImplTest {
	private final LdesFragmentRepository ldesFragmentRepository = mock(LdesFragmentRepository.class);
	private final MemberReferencesRepository memberReferencesRepository = mock(MemberReferencesRepository.class);
	private final Tracer tracer = mockTracer();

	private final FragmentationStrategyImpl fragmentationStrategy = new FragmentationStrategyImpl(
			ldesFragmentRepository,
			memberReferencesRepository, tracer, memberRepository, nonCriticalTasksExecutor);

	@Test
	void when_memberIsAddedToFragment_FragmentationStrategyImplSavesUpdatedFragment() {
		LdesFragment ldesFragment = new LdesFragment(new FragmentInfo("view", List.of()));
		Member member = mock(Member.class);
		when(member.getLdesMemberId()).thenReturn("memberId");

		fragmentationStrategy.addMemberToFragment(ldesFragment, member, any());

		verify(ldesFragmentRepository, times(1)).saveFragment(ldesFragment);
		verify(memberReferencesRepository, times(1)).saveMemberReference("memberId", "/view");
		assertEquals(List.of("memberId"), ldesFragment.getMemberIds());
	}
}