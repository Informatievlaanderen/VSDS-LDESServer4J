package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.pagination;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.BucketisedMemberRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.FragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.FragmentSequenceRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.pagination.services.OpenPageProvider;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class MemberPaginationServiceCreator {
    private final FragmentSequenceRepository fragmentSequenceRepository;
    private final BucketisedMemberRepository bucketisedMemberRepository;
    private final OpenPageProvider openPageProvider;
    private final FragmentRepository fragmentRepository;
    private final ApplicationEventPublisher eventPublisher;

    public MemberPaginationServiceCreator(FragmentSequenceRepository fragmentSequenceRepository, BucketisedMemberRepository bucketisedMemberRepository, OpenPageProvider openPageProvider, FragmentRepository fragmentRepository, ApplicationEventPublisher eventPublisher) {
        this.fragmentSequenceRepository = fragmentSequenceRepository;
        this.bucketisedMemberRepository = bucketisedMemberRepository;
        this.openPageProvider = openPageProvider;
        this.fragmentRepository = fragmentRepository;
        this.eventPublisher = eventPublisher;
    }

    public MemberPaginationService createPaginationService(ViewName viewName) {
        return new MemberPaginationService(fragmentSequenceRepository, bucketisedMemberRepository,
                openPageProvider, fragmentRepository, eventPublisher, viewName);
    }
}
