package be.vlaanderen.informatievlaanderen.ldes.server.ingest;

import be.vlaanderen.informatievlaanderen.ldes.server.ingest.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.events.MemberIngestedEvent;
import io.micrometer.core.instrument.Metrics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

// TODO: 27/04/2023 test
@Service
public class MemberIngesterImpl implements MemberIngester {

    private final MemberRepository memberRepository;

    private final ApplicationEventPublisher eventPublisher;

    private static final Logger log = LoggerFactory.getLogger(MemberIngesterImpl.class);

    public MemberIngesterImpl(MemberRepository memberRepository,
                              ApplicationEventPublisher eventPublisher) {
        this.memberRepository = memberRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public void ingest(Member member) {
        boolean memberExists = memberRepository.memberExists(member.getId());
        String memberId = member.getId().replaceAll("[\n\r\t]", "_");
        if (!memberExists) {
            Metrics.counter("ldes_server_ingested_members_count").increment();
            final Member savedMember = save(member);
            eventPublisher.publishEvent(new MemberIngestedEvent(savedMember));
            log.debug("Member with id {} ingested.", memberId);
        } else {
            log.warn("Duplicate member ingested. Member with id {} already exist", memberId);
        }
    }

    private Member save(Member member) {
        member.removeTreeMember();
        return memberRepository.saveMember(member);
    }

}
