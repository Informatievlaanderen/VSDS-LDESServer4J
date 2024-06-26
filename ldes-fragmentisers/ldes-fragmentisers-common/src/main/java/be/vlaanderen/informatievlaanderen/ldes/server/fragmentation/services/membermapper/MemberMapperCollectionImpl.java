package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.services.membermapper;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.EventStreamCreatedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.EventStreamDeletedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.EventStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class MemberMapperCollectionImpl implements MemberMapperCollection {
    private final Map<String, MemberMapper> memberMappers = new HashMap<>();
    @Override
    public Optional<MemberMapper> getMemberMapper(String collectionName) {
        Logger logger = LoggerFactory.getLogger(MemberMapperCollection.class);
        logger.info(Arrays.toString(memberMappers.keySet().toArray()));
        return Optional.ofNullable(memberMappers.get(collectionName));
    }

    @Override
    public void addMemberMapper(String collectionName, MemberMapper memberMapper) {
        memberMappers.put(collectionName, memberMapper);
    }

    @Override
    public void deleteMemberMapper(String collectionName) {
        memberMappers.remove(collectionName);
    }

    @EventListener
    public void handleEventStreamCreatedEvent(EventStreamCreatedEvent event) {
        final EventStream eventStream = event.eventStream();
        final MemberMapper memberMapper = new MemberMapper(eventStream.getVersionOfPath(), eventStream.getTimestampPath());
        addMemberMapper(eventStream.getCollection(), memberMapper);
    }

    @EventListener
    public void handleEventStreamDeletedEvent(EventStreamDeletedEvent event) {
        deleteMemberMapper(event.collectionName());
    }
}
