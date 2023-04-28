package be.vlaanderen.informatievlaanderen.ldes.server.ingest;

import be.vlaanderen.informatievlaanderen.ldes.server.ingest.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.events.MemberIngestedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.validation.MemberIngestValidator;
import io.micrometer.core.instrument.Metrics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
public class MemberIngesterImpl implements MemberIngester {

	private final MemberIngestValidator validator;
	private final MemberRepository memberRepository;
	private final ApplicationEventPublisher eventPublisher;

	private static final Logger log = LoggerFactory.getLogger(MemberIngesterImpl.class);

	public MemberIngesterImpl(MemberIngestValidator validator, MemberRepository memberRepository,
							  ApplicationEventPublisher eventPublisher) {
		this.validator = validator;
		this.memberRepository = memberRepository;
		this.eventPublisher = eventPublisher;
	}

	// TODO: 28/04/2023 include validator in testing
	@Override
	public void ingest(Member member) {
		validator.validate(member);
		final boolean isNewMember = !memberRepository.memberExists(member.getId());
		final String memberId = member.getId().replaceAll("[\n\r\t]", "_");
		if (isNewMember) {
			ingestNewMember(member, memberId);
		} else {
			log.warn("Duplicate member ingested. Member with id {} already exist", memberId);
		}
	}

	private void ingestNewMember(Member member, String memberId) {
		Metrics.counter("ldes_server_ingested_members_count").increment();
		final Member savedMember = save(member);
		eventPublisher.publishEvent(new MemberIngestedEvent(savedMember));
		log.debug("Member with id {} ingested.", memberId);
	}

	private Member save(Member member) {
		member.removeTreeMember();
		return memberRepository.saveMember(member);
	}

}
