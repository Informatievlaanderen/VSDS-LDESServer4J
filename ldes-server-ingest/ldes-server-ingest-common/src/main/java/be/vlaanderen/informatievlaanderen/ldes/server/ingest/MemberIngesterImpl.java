package be.vlaanderen.informatievlaanderen.ldes.server.ingest;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.MissingResourceException;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.collection.MemberExtractorCollection;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.entities.IngestedMember;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.extractor.MemberExtractor;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.metrics.IngestionMetricsService;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.repositories.MemberRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.validation.MemberIngestValidator;
import org.apache.jena.rdf.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

import static be.vlaanderen.informatievlaanderen.ldes.server.ingest.constants.IngestConstants.DUPLICATE_MEMBERS_DETECTED;
import static be.vlaanderen.informatievlaanderen.ldes.server.ingest.constants.IngestConstants.MEMBER_WITH_ID_INGESTED;

@Service
public class MemberIngesterImpl implements MemberIngester {
    private final MemberIngestValidator validator;
    private final MemberRepository memberRepository;
    private final MemberExtractorCollection memberExtractorCollection;
    private final IngestionMetricsService ingestionMetricsService;

    private static final Logger log = LoggerFactory.getLogger(MemberIngesterImpl.class);

    public MemberIngesterImpl(MemberIngestValidator validator, MemberRepository memberRepository,
                              MemberExtractorCollection memberExtractorCollection,
                              IngestionMetricsService ingestionMetricsService) {
        this.validator = validator;
        this.memberRepository = memberRepository;
        this.memberExtractorCollection = memberExtractorCollection;
	    this.ingestionMetricsService = ingestionMetricsService;
    }

    @Override
    public boolean ingest(String collectionName, Model ingestedModel) {
        final List<IngestedMember> members = extractMembersFromModel(collectionName, ingestedModel);

        log.atDebug().log("Validating members with size: {}", members.size());
        members.forEach(validator::validate);
        members.forEach(IngestedMember::removeTreeMember);

        int ingestedMembersCount = memberRepository.insertAll(members);

        if (ingestedMembersCount != members.size()) {
            log.warn(DUPLICATE_MEMBERS_DETECTED);
            return false;
        }
        log.atDebug().log("Successfully ingested {} members", ingestedMembersCount);
        ingestionMetricsService.incrementIngestCount(collectionName, ingestedMembersCount);
        members.forEach(member -> logSuccessfulMemberIngestion(member.getSubject()));
        return true;
    }

    private List<IngestedMember> extractMembersFromModel(String collectionName, Model model) {
        final MemberExtractor memberExtractor = memberExtractorCollection
                .getMemberExtractor(collectionName)
                .orElseThrow(() -> new MissingResourceException("eventstream", collectionName));
        return memberExtractor.extractMembers(model);
    }

    private void logSuccessfulMemberIngestion(String memberId) {
        final String loggableMemberId = memberId.replaceAll("[\n\r\t]", "_");
        log.debug(MEMBER_WITH_ID_INGESTED, loggableMemberId);
    }
}
