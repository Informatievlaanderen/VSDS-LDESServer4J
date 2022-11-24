package be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentationMediator;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.repository.MemberRepository;
import io.micrometer.core.instrument.Metrics;
import org.springframework.stereotype.Component;

@Component
public class MemberIngestServiceImpl implements MemberIngestService {

	private final MemberRepository memberRepository;

	private final FragmentationMediator fragmentationMediator;

	public MemberIngestServiceImpl(MemberRepository memberRepository,
			FragmentationMediator fragmentationMediator) {
		this.memberRepository = memberRepository;
		this.fragmentationMediator = fragmentationMediator;
	}

	@Override
	public void addMember(Member member) {
		boolean memberExists = memberRepository.memberExists(member.getLdesMemberId());
		if (!memberExists) {
			Metrics.counter("ldes_server_ingested_members_count").increment();
			Member storedMember = storeLdesMember(member);
			fragmentationMediator.addMemberToFragment(storedMember);
		}
	}

	private Member storeLdesMember(Member member) {
		member.removeTreeMember();
		return memberRepository.saveLdesMember(member);
	}
}
