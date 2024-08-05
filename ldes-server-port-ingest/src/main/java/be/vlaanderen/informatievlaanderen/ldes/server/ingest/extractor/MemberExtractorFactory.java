package be.vlaanderen.informatievlaanderen.ldes.server.ingest.extractor;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.EventStream;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.skolemization.SkolemizedMemberExtractor;

public class MemberExtractorFactory {
    private MemberExtractorFactory() {
    }

    public static MemberExtractor createMemberExtractor(EventStream eventStream) {
        final MemberExtractor baseMemberExtractor = getBaseMemberExtractor(eventStream);
        return eventStream
                .getSkolemizationDomain()
                .map(skolemizationDomain -> (MemberExtractor) new SkolemizedMemberExtractor(baseMemberExtractor, skolemizationDomain))
                .orElse(baseMemberExtractor);
    }

    private static MemberExtractor getBaseMemberExtractor(EventStream eventStream) {
        if (eventStream.isVersionCreationEnabled()) {
            return new StateObjectMemberExtractor(eventStream.getCollection());
        }
        return new VersionObjectMemberExtractor(eventStream.getCollection(), eventStream.getVersionOfPath(), eventStream.getTimestampPath());
    }
}
