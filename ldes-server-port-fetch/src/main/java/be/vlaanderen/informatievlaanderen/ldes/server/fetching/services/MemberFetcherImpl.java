package be.vlaanderen.informatievlaanderen.ldes.server.fetching.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.EventStreamCreatedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.EventStreamDeletedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.EventStream;
import be.vlaanderen.informatievlaanderen.ldes.server.fetching.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.fetching.services.versioncreation.VersionObjectCreator;
import be.vlaanderen.informatievlaanderen.ldes.server.fetching.services.versioncreation.VersionObjectCreatorFactory;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.repositories.MemberRepository;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@Service
public class MemberFetcherImpl implements MemberFetcher {
    private final Map<String, VersionObjectCreator> versionObjectCreatorMap = new HashMap<>();
    private final MemberRepository memberRepository;

    public MemberFetcherImpl(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Override
    public Stream<Member> fetchAllByIds(List<String> ids) {
        return memberRepository.findAllByIds(ids)
                .map(ingestMember -> new Member(
                        ingestMember.getSubject(),
                        versionObjectCreatorMap.get(ingestMember.getCollectionName()).createFromMember(ingestMember)
                ));
    }

    @EventListener
    public void handleEventStreamCreatedEvent(EventStreamCreatedEvent event) {
        final EventStream eventStream = event.eventStream();
        final VersionObjectCreator versionObjectCreator = VersionObjectCreatorFactory.createVersionObjectCreator(eventStream);
        versionObjectCreatorMap.put(eventStream.getCollection(), versionObjectCreator);
    }

    @EventListener
    public void handleEventStreamDeletedEvent(EventStreamDeletedEvent event) {
        versionObjectCreatorMap.remove(event.collectionName());
    }
}
