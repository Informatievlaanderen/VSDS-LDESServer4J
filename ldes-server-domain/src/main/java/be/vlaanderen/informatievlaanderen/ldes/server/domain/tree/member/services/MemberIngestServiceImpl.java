package be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.valueobjects.EventStreamDeletedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentationMediator;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.repository.MemberRepository;
import io.micrometer.core.instrument.Metrics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class MemberIngestServiceImpl implements MemberIngestService {

	private final MemberRepository memberRepository;

	private final FragmentationMediator fragmentationMediator;

	private static final Logger LOGGER = LoggerFactory.getLogger(MemberIngestServiceImpl.class);

	public MemberIngestServiceImpl(MemberRepository memberRepository,
			FragmentationMediator fragmentationMediator) {
		this.memberRepository = memberRepository;
		this.fragmentationMediator = fragmentationMediator;
	}

	@Override
	public void addMember(Member member) {
		boolean memberExists = memberRepository.memberExists(member.getLdesMemberId());
		String memberId = member.getLdesMemberId().replaceAll("[\n\r\t]", "_");
		if (!memberExists) {
			Metrics.counter("ldes_server_ingested_members_count").increment();
			storeLdesMember(member);
			fragmentationMediator.addMemberToFragment(member);
			LOGGER.debug("Member with id {} ingested.", memberId);
		} else {
			LOGGER.warn("Duplicate member ingested. Member with id {} already exist", memberId);
		}
	}

	@EventListener
	public void handleEventStreamDeletedEvent(EventStreamDeletedEvent event) {
		memberRepository.deleteMembersByCollection(event.collectionName());
	}

	private Member storeLdesMember(Member member) {
		member.removeTreeMember();
		return memberRepository.saveLdesMember(member);
	}
}
