package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.FragmentInfo;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.springframework.cloud.sleuth.Tracer;

import java.util.List;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.TracerMockHelper.mockTracer;
import static org.mockito.Mockito.*;

class FragmentationStrategyImplTest {
	private final LdesFragmentRepository ldesFragmentRepository = mock(LdesFragmentRepository.class);
	private final MemberRepository memberRepository = mock(MemberRepository.class);
	private final Tracer tracer = mockTracer();

	private final FragmentationStrategyImpl fragmentationStrategy = new FragmentationStrategyImpl(
			ldesFragmentRepository,
			memberRepository, tracer);

	@Test
	void when_memberIsAddedToFragment_FragmentationStrategyImplSavesUpdatedFragment() {
		LdesFragment ldesFragment = new LdesFragment(new FragmentInfo("view", List.of()));
		Member member = mock(Member.class);
		when(member.getLdesMemberId()).thenReturn("memberId");

		fragmentationStrategy.addMemberToFragment(ldesFragment, member, any());

		verify(ldesFragmentRepository, times(1)).addMemberToFragment(ldesFragment, "memberId");
		verify(memberRepository, times(1)).addMemberReference("memberId", "/view");
	}
}