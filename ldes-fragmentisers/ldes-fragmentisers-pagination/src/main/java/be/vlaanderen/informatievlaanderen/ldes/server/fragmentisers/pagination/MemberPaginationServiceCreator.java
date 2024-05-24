package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.pagination;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ConfigProperties;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewSpecification;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.BucketisedMemberRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.FragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.FragmentSequenceRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.pagination.config.PaginationConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.pagination.services.OpenPageProvider;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.pagination.services.PageCreator;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.pagination.config.PaginationProperties.BIDIRECTIONAL_RELATIONS;
import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.pagination.config.PaginationProperties.MEMBER_LIMIT;

@Component
public class MemberPaginationServiceCreator {
    private final FragmentSequenceRepository fragmentSequenceRepository;
    private final BucketisedMemberRepository bucketisedMemberRepository;
    private final FragmentRepository fragmentRepository;
    private final ApplicationEventPublisher eventPublisher;

    public MemberPaginationServiceCreator(FragmentSequenceRepository fragmentSequenceRepository,
                                          BucketisedMemberRepository bucketisedMemberRepository,
                                          FragmentRepository fragmentRepository,
                                          ApplicationEventPublisher eventPublisher) {
        this.fragmentSequenceRepository = fragmentSequenceRepository;
        this.bucketisedMemberRepository = bucketisedMemberRepository;
        this.fragmentRepository = fragmentRepository;
        this.eventPublisher = eventPublisher;
    }

    public MemberPaginationService createPaginationService(ViewName viewName, ViewSpecification view) {
        OpenPageProvider openPageProvider = getOpenPageProvider(view.getPaginationProperties());

        return new MemberPaginationService(fragmentSequenceRepository, bucketisedMemberRepository,
                openPageProvider, fragmentRepository, eventPublisher, viewName);
    }

    private OpenPageProvider getOpenPageProvider(ConfigProperties properties) {
        PaginationConfig paginationConfig = createPaginationConfig(properties);
        PageCreator pageFragmentCreator = getPageCreator(paginationConfig.bidirectionalRelations());
        return new OpenPageProvider(pageFragmentCreator, fragmentRepository,
                paginationConfig.memberLimit());
    }

    private PageCreator getPageCreator(boolean bidirectionalRelations) {
        return new PageCreator(
                fragmentRepository, bidirectionalRelations);
    }

    private PaginationConfig createPaginationConfig(ConfigProperties properties) {
        return new PaginationConfig(Long.valueOf(properties.get(MEMBER_LIMIT)),
                Boolean.parseBoolean(properties.getOrDefault(BIDIRECTIONAL_RELATIONS, "true")));
    }
}
