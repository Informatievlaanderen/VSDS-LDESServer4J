package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.fragmentation.MemberBucketisedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.BucketisedMember;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.BucketisedMemberRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class CachedBucketisedMemberSaver implements BucketisedMemberSaver {
    private final List<BucketisedMember> cachedMembers = new ArrayList<>();
    private final BucketisedMemberRepository repository;
    private final ApplicationEventPublisher eventPublisher;

    public CachedBucketisedMemberSaver(BucketisedMemberRepository repository, ApplicationEventPublisher eventPublisher) {
        this.repository = repository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public void addBucketisedMember(BucketisedMember bucketisedMember) {
        cachedMembers.add(bucketisedMember);
    }

    @Override
    public void flush() {
        if(cachedMembers.isEmpty()) {
            return;
        }
        repository.insertAll(List.copyOf(cachedMembers));
        eventPublisher.publishEvent(new MemberBucketisedEvent(cachedMembers.getFirst().viewName()));
        cachedMembers.clear();
    }
}
