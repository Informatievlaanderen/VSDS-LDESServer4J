package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.memberreferences.entities.MemberReferencesRepository;
import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.Tracer;

public class FragmentationStrategyImpl implements FragmentationStrategy {
	private final LdesFragmentRepository ldesFragmentRepository;
	private final MemberReferencesRepository memberReferencesRepository;
	private final Tracer tracer;

	public FragmentationStrategyImpl(LdesFragmentRepository ldesFragmentRepository,
			MemberReferencesRepository memberReferencesRepository, Tracer tracer) {
		this.ldesFragmentRepository = ldesFragmentRepository;
		this.memberReferencesRepository = memberReferencesRepository;
		this.tracer = tracer;
	}

	@Override
	public void addMemberToFragment(LdesFragment ldesFragment, Member member, Span parentSpan) {
		Span finalSpan = tracer.nextSpan(parentSpan).name("add member to fragment").start();
		ldesFragment.addMember(member.getLdesMemberId());
		ldesFragmentRepository.addMemberToFragment(ldesFragment, member.getLdesMemberId());
//		ldesFragmentRepository.saveFragment(ldesFragment);
		memberReferencesRepository.addMemberReference(member.getLdesMemberId(), ldesFragment.getFragmentId());
//		memberReferencesRepository.saveMemberReference(member.getLdesMemberId(), ldesFragment.getFragmentId());
		finalSpan.end();
	}

}
