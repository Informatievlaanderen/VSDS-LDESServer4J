package be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentationMediator;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.repository.MemberRepository;
import io.micrometer.core.instrument.Metrics;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class MemberIngestServiceImpl implements MemberIngestService {

	private final MemberRepository memberRepository;

	private final FragmentationMediator fragmentationMediator;
	private final ExecutorService executor;

	public MemberIngestServiceImpl(MemberRepository memberRepository,
			FragmentationMediator fragmentationMediator) {
		this.memberRepository = memberRepository;
		this.fragmentationMediator = fragmentationMediator;
		this.executor = Executors.newFixedThreadPool(10);
	}

	@Override
	public void addMember(Member member) {
		boolean memberExists = memberRepository.memberExists(member.getLdesMemberId());
		if (!memberExists) {
			Metrics.counter("ldes_server_ingested_members_count").increment();
			executor.submit(() -> storeLdesMember(member));
			fragmentationMediator.addMemberToFragment(member);
		}
	}

	private Member storeLdesMember(Member member) {
		member.removeTreeMember();
		return memberRepository.saveLdesMember(member);
	}
}
