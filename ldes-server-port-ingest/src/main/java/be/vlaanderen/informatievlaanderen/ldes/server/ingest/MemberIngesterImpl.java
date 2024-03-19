package be.vlaanderen.informatievlaanderen.ldes.server.ingest;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.ingest.MemberIngestedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.MissingResourceException;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.collection.MemberExtractorCollection;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.extractor.MemberExtractor;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.repositories.MemberRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.validation.MemberIngestValidator;
import io.micrometer.core.instrument.Metrics;
import org.apache.jena.rdf.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MemberIngesterImpl implements MemberIngester {

    private static final String LDES_SERVER_INGESTED_MEMBERS_COUNT = "ldes_server_ingested_members_count";
    private static final String MEMBER_WITH_ID_INGESTED = "Member with id {} ingested.";
    private static final String DUPLICATE_MEMBERS_DETECTED = "Duplicate members detected. Member(s) are ignored";

    private final MemberIngestValidator validator;
    private final MemberRepository memberRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final MemberExtractorCollection memberExtractorCollection;

    private static final Logger log = LoggerFactory.getLogger(MemberIngesterImpl.class);

    public MemberIngesterImpl(MemberIngestValidator validator, MemberRepository memberRepository,
                              ApplicationEventPublisher eventPublisher, MemberExtractorCollection memberExtractorCollection) {
        this.validator = validator;
        this.memberRepository = memberRepository;
        this.eventPublisher = eventPublisher;
        this.memberExtractorCollection = memberExtractorCollection;
    }

    @Override
    public boolean ingest(String collectionName, Model ingestedModel) {
        final MemberExtractor memberExtractor = memberExtractorCollection
                .getMemberExtractor(collectionName)
                .orElseThrow(() -> new MissingResourceException("eventstream", collectionName));
        final List<Member> members = memberExtractor.extractMembers(ingestedModel);
        members.forEach(validator::validate);

        return insertIntoRepo(members);
    }

    private boolean insertIntoRepo(List<Member> members) {
        members.forEach(Member::removeTreeMember);
        List<Member> insertedMembers = memberRepository.insertAll(members);
        if (insertedMembers.size() != members.size()) {
            log.warn(DUPLICATE_MEMBERS_DETECTED);
            return false;
        }
        insertedMembers.forEach(this::handleSuccessfulMembersInsertion);
        return true;
    }

    private void handleSuccessfulMembersInsertion(Member member) {
        final String memberId = member.getId().replaceAll("[\n\r\t]", "_");
        final var memberIngestedEvent = new MemberIngestedEvent(member.getId(),
                member.getCollectionName(),
                member.getSequenceNr(),
                member.getVersionOf(),
                member.getTimestamp());
        eventPublisher.publishEvent(memberIngestedEvent);
        Metrics.counter(LDES_SERVER_INGESTED_MEMBERS_COUNT).increment();
        log.debug(MEMBER_WITH_ID_INGESTED, memberId);
    }
}
