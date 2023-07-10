package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.fragmentation.MemberAllocatedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.repository.MemberRepository;
import io.micrometer.observation.Observation;
import org.springframework.context.ApplicationEventPublisher;

public class FragmentationStrategyImpl implements FragmentationStrategy {
	private final MemberRepository memberRepository;
	private final LdesFragmentRepository ldesFragmentRepository;
	private final NonCriticalTasksExecutor nonCriticalTasksExecutor;
	private final ApplicationEventPublisher applicationEventPublisher;

	public FragmentationStrategyImpl(LdesFragmentRepository ldesFragmentRepository,
			MemberRepository memberRepository,
			NonCriticalTasksExecutor nonCriticalTasksExecutor, ApplicationEventPublisher applicationEventPublisher) {
		this.memberRepository = memberRepository;
		this.nonCriticalTasksExecutor = nonCriticalTasksExecutor;
		this.ldesFragmentRepository = ldesFragmentRepository;
		this.applicationEventPublisher = applicationEventPublisher;
	}

	@Override
	public void addMemberToFragment(LdesFragment ldesFragment, Member member, Observation parentObservation) {
		nonCriticalTasksExecutor.submit(
				() -> memberRepository.addMemberReference(member.getLdesMemberId(),
						ldesFragment.getFragmentIdString()));
		ldesFragmentRepository.incrementNumberOfMembers(ldesFragment.getFragmentId());
		applicationEventPublisher
				.publishEvent(new MemberAllocatedEvent(member.getLdesMemberId(), ldesFragment.getViewName()));
	}

}
