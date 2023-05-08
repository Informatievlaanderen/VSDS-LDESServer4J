package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.repository.MemberRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;
import org.springframework.stereotype.Service;

import java.util.stream.Stream;

@Service
public class LdesFragmentRemoverImpl implements LdesFragmentRemover {
	private final LdesFragmentRepository ldesFragmentRepository;
	private final MemberRepository memberRepository;

	public LdesFragmentRemoverImpl(LdesFragmentRepository ldesFragmentRepository, MemberRepository memberRepository) {
		this.ldesFragmentRepository = ldesFragmentRepository;
		this.memberRepository = memberRepository;
	}

	@Override
	public void removeLdesFragmentsOfView(ViewName viewName) {
		Stream<LdesFragment> ldesFragments = ldesFragmentRepository.retrieveFragmentsOfView(viewName.asString());
		ldesFragments.forEach(ldesFragment -> {
			Stream<Member> membersByReference = memberRepository.getMembersByReference(ldesFragment.getFragmentId());
			membersByReference.forEach(member -> memberRepository.removeMemberReference(member.getLdesMemberId(),
					ldesFragment.getFragmentId()));
		});
		ldesFragmentRepository.removeLdesFragmentsOfView(viewName.asString());
	}
}
