package be.vlaanderen.informatievlaanderen.ldes.server.ingest;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.ingest.MemberIngestedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.repositories.MemberRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.validation.MemberIngestValidator;
import io.micrometer.core.instrument.Metrics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.Optional;

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

    @Override
    public void ingest(Member member) {
        validator.validate(member);
        final String memberId = member.getId().replaceAll("[\n\r\t]", "_");
        ingestNewMember(member, memberId);
    }

    private void ingestNewMember(Member member, String memberId) {
        Optional<Member> memberSaved = insert(member);
        if (memberSaved.isPresent()) {
            Member savedMember = memberSaved.get();
            Metrics.counter("ldes_server_ingested_members_count").increment();
            final var memberIngestedEvent = new MemberIngestedEvent(savedMember.getModel(), savedMember.getId(),
                    savedMember.getCollectionName(), savedMember.getSequenceNr());
            eventPublisher.publishEvent(memberIngestedEvent);
            log.debug("Member with id {} ingested.", memberId);
        } else {
            log.warn("Duplicate member ingested. Member with id {} already exists", memberId);
        }
    }

    private Optional<Member> insert(Member member) {
        member.removeTreeMember();
        return memberRepository.insertMember(member);
    }

}