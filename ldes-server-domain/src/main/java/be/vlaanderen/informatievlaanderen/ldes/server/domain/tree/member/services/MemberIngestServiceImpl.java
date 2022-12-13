package be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentationMediator;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.NonCriticalTasksExecutor;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.repository.MemberRepository;
import io.micrometer.core.instrument.Metrics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class MemberIngestServiceImpl implements MemberIngestService {

	private final MemberRepository memberRepository;

	private final FragmentationMediator fragmentationMediator;
	private final NonCriticalTasksExecutor nonCriticalTasksExecutor;

	private static final Logger LOGGER = LoggerFactory.getLogger(MemberIngestServiceImpl.class);

	public MemberIngestServiceImpl(MemberRepository memberRepository,
			FragmentationMediator fragmentationMediator, NonCriticalTasksExecutor nonCriticalTasksExecutor) {
		this.memberRepository = memberRepository;
		this.fragmentationMediator = fragmentationMediator;
		this.nonCriticalTasksExecutor = nonCriticalTasksExecutor;
	}

	@Override
	public void addMember(Member member) {
		boolean memberExists = memberRepository.memberExists(member.getLdesMemberId());
		if (!memberExists) {
			Metrics.counter("ldes_server_ingested_members_count").increment();
			nonCriticalTasksExecutor.submit(() -> storeLdesMember(member));
			fragmentationMediator.addMemberToFragment(member);
			LOGGER.debug("Member with id " + member.getLdesMemberId() + " ingested.");
		}
		else {
			LOGGER.warn("Duplicate member ingested. Member with id " + member.getLdesMemberId() + " already exist");
		}
	}

	private Member storeLdesMember(Member member) {
		member.removeTreeMember();
		return memberRepository.saveLdesMember(member);
	}
}
