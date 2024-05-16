package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.pagination;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.fragmentation.MemberBucketisedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.BucketisedMemberRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.FragmentRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;

import java.util.HashMap;
import java.util.Map;

public class PaginationService {

    private final Map<ViewName, MemberPaginationService> map = new HashMap<>();
    private final FragmentRepository fragmentRepository;
    private final BucketisedMemberRepository bucketisedMemberRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final PaginationStrategy paginationStrategy;

    public PaginationService(FragmentRepository fragmentRepository, BucketisedMemberRepository bucketisedMemberRepository, ApplicationEventPublisher eventPublisher, PaginationStrategy paginationStrategy) {
        this.fragmentRepository = fragmentRepository;
        this.bucketisedMemberRepository = bucketisedMemberRepository;
        this.eventPublisher = eventPublisher;
        this.paginationStrategy = paginationStrategy;
    }

    @EventListener
    public void handleMemberBucketisedEvent(MemberBucketisedEvent event) {
        ViewName viewName = event.viewName();
        MemberPaginationService paginationService = map.get(viewName);
        bucketisedMemberRepository.getFirstUnallocatedMember(, )
        paginationStrategy.addMemberToFragment()
    }
}
