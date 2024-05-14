package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.fragmentation.MemberBucketisedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.BucketisedMember;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.BucketisedMemberRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BucketisedMemberSaverImpl {
    private final BucketisedMemberRepository repository;
    private final ApplicationEventPublisher eventPublisher;

    public BucketisedMemberSaverImpl(BucketisedMemberRepository repository, ApplicationEventPublisher eventPublisher) {
        this.repository = repository;
        this.eventPublisher = eventPublisher;
    }

    public void save(List<BucketisedMember> members) {
        repository.insertAll(members);
        eventPublisher.publishEvent(new MemberBucketisedEvent(members.getFirst().viewName()));
    }
}
