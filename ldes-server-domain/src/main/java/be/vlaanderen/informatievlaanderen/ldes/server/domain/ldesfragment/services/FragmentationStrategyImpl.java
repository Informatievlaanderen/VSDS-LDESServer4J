package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.repository.MemberRepository;
import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.Tracer;

public class FragmentationStrategyImpl implements FragmentationStrategy {
	private final LdesFragmentRepository ldesFragmentRepository;
	private final MemberRepository memberRepository;
	private final Tracer tracer;

	public FragmentationStrategyImpl(LdesFragmentRepository ldesFragmentRepository,
			MemberRepository memberRepository, Tracer tracer) {
		this.ldesFragmentRepository = ldesFragmentRepository;
		this.memberRepository = memberRepository;
		this.tracer = tracer;
	}

	@Override
	public void addMemberToFragment(LdesFragment ldesFragment, Member member, Span parentSpan) {
		Span finalSpan = tracer.nextSpan(parentSpan).name("add member to fragment").start();

		memberRepository.addMemberReference(member.getLdesMemberId(), ldesFragment.getFragmentId());
		ldesFragmentRepository.incrementNumberOfMembers(ldesFragment);
		finalSpan.end();
	}

}
