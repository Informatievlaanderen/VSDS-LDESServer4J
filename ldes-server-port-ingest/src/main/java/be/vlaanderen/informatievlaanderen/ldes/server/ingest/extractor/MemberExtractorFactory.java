package be.vlaanderen.informatievlaanderen.ldes.server.ingest.extractor;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.EventStream;

public class MemberExtractorFactory {
    private MemberExtractorFactory() {
    }

    public static MemberExtractor createMemberExtractor(EventStream eventStream) {
        if (eventStream.isVersionCreationEnabled()) {
            return new StateObjectMemberExtractor(eventStream.getCollection());
        }
        return new VersionObjectMemberExtractor(eventStream.getCollection(), eventStream.getVersionOfPath(), eventStream.getTimestampPath());
    }
}
