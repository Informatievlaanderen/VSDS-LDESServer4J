package be.vlaanderen.informatievlaanderen.ldes.server.ingest.collection;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.EventStreamCreatedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.EventStreamDeletedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.MissingResourceException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.EventStream;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.transformers.VersionObjectExtractor;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.transformers.VersionObjectTransformer;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class VersionObjectTransformerCollectionImpl implements VersionObjectTransformerCollection {
    private final Map<String, VersionObjectTransformer> versionObjectTransformers = new HashMap<>();

    @Override
    public VersionObjectTransformer getVersionObjectTransformer(String collectionName) {
        final VersionObjectTransformer versionObjectTransformer = versionObjectTransformers.get(collectionName);
        if (versionObjectTransformer == null) {
            throw new MissingResourceException("eventstream", collectionName);
        }
        return versionObjectTransformer;
    }

    @Override
    public void addVersionObjectTransformer(String collectionName, VersionObjectTransformer versionObjectTransformer) {
        versionObjectTransformers.put(collectionName, versionObjectTransformer);
    }

    @Override
    public void deleteVersionObjectTransformer(String collectionName) {
        versionObjectTransformers.remove(collectionName);
    }

    @EventListener
    public void handleEventStreamCreatedEvent(EventStreamCreatedEvent event) {
        final EventStream eventStream = event.eventStream();
        final VersionObjectTransformer versionObjectTransformer;
        if (!eventStream.isVersionCreationEnabled()) {
            versionObjectTransformer = new VersionObjectExtractor(
                    eventStream.getCollection(),
                    eventStream.getVersionOfPath(),
                    eventStream.getTimestampPath()
            );
        } else {
            versionObjectTransformer = null;
        }

        versionObjectTransformers.put(eventStream.getCollection(), versionObjectTransformer);
    }

    @EventListener
    public void handleEventStreamDeletedEvent(EventStreamDeletedEvent event) {
        deleteVersionObjectTransformer(event.collectionName());
    }
}
