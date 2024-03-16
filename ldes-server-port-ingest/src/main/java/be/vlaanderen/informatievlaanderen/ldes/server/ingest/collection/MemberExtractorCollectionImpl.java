package be.vlaanderen.informatievlaanderen.ldes.server.ingest.collection;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.EventStreamCreatedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.EventStreamDeletedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.EventStream;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.extractor.MemberExtractor;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.extractor.MemberExtractorFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class MemberExtractorCollectionImpl implements MemberExtractorCollection {
    private final Map<String, MemberExtractor> versionObjectTransformers = new HashMap<>();

    @Override
    public Optional<MemberExtractor> getMemberExtractor(String collectionName) {
        return Optional.ofNullable(versionObjectTransformers.get(collectionName));
    }

    @Override
    public void addMemberExtractor(String collectionName, MemberExtractor memberExtractor) {
        versionObjectTransformers.put(collectionName, memberExtractor);
    }

    @Override
    public void deleteMemberExtractor(String collectionName) {
        versionObjectTransformers.remove(collectionName);
    }

    @EventListener
    public void handleEventStreamCreatedEvent(EventStreamCreatedEvent event) {
        final EventStream eventStream = event.eventStream();
        addMemberExtractor(eventStream.getCollection(), MemberExtractorFactory.createMemberExtractor(eventStream));
    }

    @EventListener
    public void handleEventStreamDeletedEvent(EventStreamDeletedEvent event) {
        deleteMemberExtractor(event.collectionName());
    }
}
