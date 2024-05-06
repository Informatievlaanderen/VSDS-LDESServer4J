package be.vlaanderen.informatievlaanderen.ldes.server.ingest;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.ingest.MembersIngestedEvent;
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
        final List<Member> members = extractMembersFromModel(collectionName, ingestedModel);

        members.forEach(validator::validate);
        members.forEach(Member::removeTreeMember);

        int ingestedMembersCount = memberRepository.insertAll(members).size();

        if (ingestedMembersCount != members.size()) {
            log.warn(DUPLICATE_MEMBERS_DETECTED);
            log.info("HIT duplicate");
            return false;
        }
        log.info("HIT ingested {}", members.get(0).getId());
        publishIngestedEvent(collectionName, members);
        Metrics.counter(LDES_SERVER_INGESTED_MEMBERS_COUNT).increment(ingestedMembersCount);
        members.forEach(member -> logSuccessfulMemberIngestion(member.getId()));
        return true;
    }

    private List<Member> extractMembersFromModel(String collectionName, Model model) {
        final MemberExtractor memberExtractor = memberExtractorCollection
                .getMemberExtractor(collectionName)
                .orElseThrow(() -> new MissingResourceException("eventstream", collectionName));
        return memberExtractor.extractMembers(model);
    }

    private void publishIngestedEvent(String collectionName, List<Member> members) {
        final List<MembersIngestedEvent.MemberProperties> memberProperties = members.stream()
                .map(member -> new MembersIngestedEvent.MemberProperties(member.getId(), member.getVersionOf(), member.getTimestamp()))
                .toList();
        eventPublisher.publishEvent(new MembersIngestedEvent(collectionName, memberProperties));
    }


    private void logSuccessfulMemberIngestion(String memberId) {
        final String loggableMemberId = memberId.replaceAll("[\n\r\t]", "_");
        log.debug(MEMBER_WITH_ID_INGESTED, loggableMemberId);
    }
}
