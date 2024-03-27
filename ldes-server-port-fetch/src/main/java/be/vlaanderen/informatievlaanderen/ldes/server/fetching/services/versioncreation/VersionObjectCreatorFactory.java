package be.vlaanderen.informatievlaanderen.ldes.server.fetching.services.versioncreation;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.EventStream;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.entities.Member;

public class VersionObjectCreatorFactory {
    private VersionObjectCreatorFactory() {
    }

    public static VersionObjectCreator createVersionObjectCreator(EventStream eventStream) {
        if(!eventStream.isVersionCreationEnabled()) {
            return Member::getModel;
        }
        return new VersionObjectCreatorImpl(eventStream.getVersionOfPath(), eventStream.getTimestampPath());
    }
}
