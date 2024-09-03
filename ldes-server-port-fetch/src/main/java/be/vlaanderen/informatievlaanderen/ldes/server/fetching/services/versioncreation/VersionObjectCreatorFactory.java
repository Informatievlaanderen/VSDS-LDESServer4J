package be.vlaanderen.informatievlaanderen.ldes.server.fetching.services.versioncreation;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.EventStream;

public class VersionObjectCreatorFactory {
    private VersionObjectCreatorFactory() {
    }

    public static VersionObjectCreator createVersionObjectCreator(EventStream eventStream) {
        if(!eventStream.isVersionCreationEnabled()) {
            return (subject, model, versionOf, timestamp) -> model;
        }
        return new VersionObjectCreatorImpl(eventStream.getVersionOfPath(), eventStream.getTimestampPath());
    }
}
