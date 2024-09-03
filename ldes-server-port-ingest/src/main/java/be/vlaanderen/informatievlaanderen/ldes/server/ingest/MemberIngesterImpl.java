package be.vlaanderen.informatievlaanderen.ldes.server.ingest;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.ingest.MembersIngestedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.MissingResourceException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.services.ServerMetrics;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.collection.MemberExtractorCollection;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.entities.IngestedMember;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.extractor.MemberExtractor;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.repositories.MemberRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.validation.MemberIngestValidator;
import org.apache.jena.rdf.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static be.vlaanderen.informatievlaanderen.ldes.server.ingest.constants.IngestConstants.DUPLICATE_MEMBERS_DETECTED;
import static be.vlaanderen.informatievlaanderen.ldes.server.ingest.constants.IngestConstants.MEMBER_WITH_ID_INGESTED;

@Service
public class MemberIngesterImpl implements MemberIngester {
    private final MemberIngestValidator validator;
    private final MemberRepository memberRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final MemberExtractorCollection memberExtractorCollection;
    private final ServerMetrics serverMetrics;

    private static final Logger log = LoggerFactory.getLogger(MemberIngesterImpl.class);

    public MemberIngesterImpl(MemberIngestValidator validator, MemberRepository memberRepository,
                              ApplicationEventPublisher eventPublisher, MemberExtractorCollection memberExtractorCollection,
                              ServerMetrics serverMetrics) {
        this.validator = validator;
        this.memberRepository = memberRepository;
        this.eventPublisher = eventPublisher;
        this.memberExtractorCollection = memberExtractorCollection;
	    this.serverMetrics = serverMetrics;
    }

    @Override
    public boolean ingest(String collectionName, Model ingestedModel) {
        final List<IngestedMember> members = extractMembersFromModel(collectionName, ingestedModel);

        members.forEach(validator::validate);
        members.forEach(IngestedMember::removeTreeMember);

        int ingestedMembersCount = memberRepository.insertAll(members).size();

        if (ingestedMembersCount != members.size()) {
            log.warn(DUPLICATE_MEMBERS_DETECTED);
            return false;
        }
        publishIngestedEvent(collectionName, members);
        serverMetrics.incrementIngestCount(collectionName, ingestedMembersCount);
        members.forEach(member -> logSuccessfulMemberIngestion(member.getSubject()));
        return true;
    }

    private List<IngestedMember> extractMembersFromModel(String collectionName, Model model) {
        final MemberExtractor memberExtractor = memberExtractorCollection
                .getMemberExtractor(collectionName)
                .orElseThrow(() -> new MissingResourceException("eventstream", collectionName));
        return memberExtractor.extractMembers(model);
    }

    private void publishIngestedEvent(String collectionName, List<IngestedMember> members) {
        CompletableFuture.runAsync(() -> {
            final List<MembersIngestedEvent.MemberProperties> memberProperties = members.stream()
                    .map(member -> new MembersIngestedEvent.MemberProperties(member.getCollectionName() + "/" + member.getSubject(), member.getVersionOf(), member.getTimestamp()))
                    .toList();
            eventPublisher.publishEvent(new MembersIngestedEvent(collectionName, memberProperties));
        });
    }

    private void logSuccessfulMemberIngestion(String memberId) {
        final String loggableMemberId = memberId.replaceAll("[\n\r\t]", "_");
        log.debug(MEMBER_WITH_ID_INGESTED, loggableMemberId);
    }
}
