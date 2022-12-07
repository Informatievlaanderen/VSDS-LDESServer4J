package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.NonCriticalTasksExecutor;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.repository.MemberRepository;
import org.springframework.cloud.sleuth.Span;

public class FragmentationStrategyImpl implements FragmentationStrategy {
	private final MemberRepository memberRepository;
	private final LdesFragmentRepository ldesFragmentRepository;
	private final NonCriticalTasksExecutor nonCriticalTasksExecutor;

	public FragmentationStrategyImpl(LdesFragmentRepository ldesFragmentRepository,
			MemberRepository memberRepository,
			NonCriticalTasksExecutor nonCriticalTasksExecutor) {
		this.memberRepository = memberRepository;
		this.nonCriticalTasksExecutor = nonCriticalTasksExecutor;
		this.ldesFragmentRepository = ldesFragmentRepository;
	}

	@Override
	public void addMemberToFragment(LdesFragment ldesFragment, Member member, Span parentSpan) {
		nonCriticalTasksExecutor.submit(
				() -> memberRepository.addMemberReference(member.getLdesMemberId(), ldesFragment.getFragmentId()));
		ldesFragmentRepository.incrementNumberOfMembers(ldesFragment.getFragmentId());
	}

}
