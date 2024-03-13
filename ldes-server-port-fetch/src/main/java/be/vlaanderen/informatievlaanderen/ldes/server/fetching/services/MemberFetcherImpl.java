package be.vlaanderen.informatievlaanderen.ldes.server.fetching.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.EventStreamCreatedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.EventStreamDeletedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.EventStream;
import be.vlaanderen.informatievlaanderen.ldes.server.fetching.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.fetching.valueobjects.EventStreamProperties;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.repositories.MemberRepository;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MemberFetcherImpl implements MemberFetcher {
    private final Map<String, EventStreamProperties> eventStreamPropertiesMap = new HashMap<>();
    private final MemberRepository memberRepository;

    public MemberFetcherImpl(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Override
    public List<Member> fetchAllByIds(List<String> ids) {
        return memberRepository.findAllByIds(ids)
                .stream().map(ingestMember -> new Member(
                        ingestMember.getId(),
                        eventStreamPropertiesMap.get(ingestMember.getCollectionName()),
                        ingestMember.getVersionOf(),
                        ingestMember.getTimestamp(),
                        ingestMember.getModel()
                ))
                .toList();
    }

    @EventListener
    public void handleEventStreamCreatedEvent(EventStreamCreatedEvent event) {
        final EventStream eventStream = event.eventStream();
        final EventStreamProperties eventStreamProperties = new EventStreamProperties(eventStream.getVersionOfPath(), eventStream.getTimestampPath());
        eventStreamPropertiesMap.put(eventStream.getCollection(), eventStreamProperties);
    }

    @EventListener
    public void handleEventStreamDeletedEvent(EventStreamDeletedEvent event) {
        eventStreamPropertiesMap.remove(event.collectionName());
    }
}
