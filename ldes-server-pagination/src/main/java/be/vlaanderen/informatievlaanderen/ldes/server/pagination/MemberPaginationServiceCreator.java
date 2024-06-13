package be.vlaanderen.informatievlaanderen.ldes.server.pagination;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ConfigProperties;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewSpecification;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.FragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.pagination.config.PaginationConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.pagination.config.PaginationProperties;
import be.vlaanderen.informatievlaanderen.ldes.server.pagination.services.OpenPageProvider;
import be.vlaanderen.informatievlaanderen.ldes.server.pagination.services.PageCreator;
import org.springframework.stereotype.Component;

@Component
public class MemberPaginationServiceCreator {
    private final FragmentRepository fragmentRepository;

    public MemberPaginationServiceCreator(FragmentRepository fragmentRepository) {
        this.fragmentRepository = fragmentRepository;
    }

    public MemberPaginationService createPaginationService(ViewSpecification view) {
        OpenPageProvider openPageProvider = getOpenPageProvider(view.getPaginationProperties());

        return new MemberPaginationService(fragmentRepository, openPageProvider, view.getPageSize());
    }

    private OpenPageProvider getOpenPageProvider(ConfigProperties properties) {
        PaginationConfig paginationConfig = createPaginationConfig(properties);
        PageCreator pageFragmentCreator = getPageCreator(paginationConfig.bidirectionalRelations());
        return new OpenPageProvider(pageFragmentCreator, fragmentRepository,
                paginationConfig.memberLimit());
    }

    private PageCreator getPageCreator(boolean bidirectionalRelations) {
        return new PageCreator(fragmentRepository, bidirectionalRelations);
    }

    private PaginationConfig createPaginationConfig(ConfigProperties properties) {
        return new PaginationConfig(Long.valueOf(properties.get(PaginationProperties.MEMBER_LIMIT)),
                Boolean.parseBoolean(properties.getOrDefault(PaginationProperties.BIDIRECTIONAL_RELATIONS, "true")));
    }
}
