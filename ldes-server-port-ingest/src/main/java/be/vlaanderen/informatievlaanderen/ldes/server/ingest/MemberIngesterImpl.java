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

    private static final String LDES_SERVER_INGESTED_MEMBERS_COUNT = "ldes_server_ingested_members_count";
    private static final String MEMBER_WITH_ID_INGESTED = "Member with id {} ingested.";
    private static final String DUPLICATE_MEMBER_INGESTED_MEMBER_WITH_ID_ALREADY_EXISTS = "Duplicate member ingested. Member with id {} already exists. Duplicate member is ignored";
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
    public boolean ingest(Member member) {
        validator.validate(member);
        final String memberId = member.getId().replaceAll("[\n\r\t]", "_");
        Optional<Member> ingestedMember = insertIntoRepo(member);
        ingestedMember.ifPresentOrElse(insertedMember -> handleSuccessfulMemberInsertion(insertedMember, memberId),
                () -> log.warn(DUPLICATE_MEMBER_INGESTED_MEMBER_WITH_ID_ALREADY_EXISTS, memberId)
        );
        return ingestedMember.isPresent();
    }


    private void handleSuccessfulMemberInsertion(Member member, String memberId) {
        final var memberIngestedEvent = new MemberIngestedEvent(member.getModel(), member.getId(),
                member.getCollectionName(), member.getSequenceNr());
        eventPublisher.publishEvent(memberIngestedEvent);
        Metrics.counter(LDES_SERVER_INGESTED_MEMBERS_COUNT).increment();
        log.debug(MEMBER_WITH_ID_INGESTED, memberId);
    }

    private Optional<Member> insertIntoRepo(Member member) {
        member.removeTreeMember();
        return memberRepository.insert(member);
    }

}
