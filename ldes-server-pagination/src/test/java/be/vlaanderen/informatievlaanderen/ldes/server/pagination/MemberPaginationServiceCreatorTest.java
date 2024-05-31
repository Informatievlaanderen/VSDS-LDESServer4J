package be.vlaanderen.informatievlaanderen.ldes.server.pagination;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewSpecification;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.BucketisedMemberRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.FragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.pagination.repositories.PaginationSequenceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class MemberPaginationServiceCreatorTest {
    private final ViewName VIEW_NAME = new ViewName("collection", "view");
    private final ViewSpecification VIEW = new ViewSpecification(VIEW_NAME, List.of(), List.of(), 50);
    private PaginationSequenceRepository sequenceRepository;
    private BucketisedMemberRepository bucketisedMemberRepository;
    private FragmentRepository fragmentRepository;
    private ApplicationEventPublisher eventPublisher;
    private MemberPaginationServiceCreator memberPaginationServiceCreator;

    @BeforeEach
    void setUp() {
        sequenceRepository = mock(PaginationSequenceRepository.class);
        bucketisedMemberRepository = mock(BucketisedMemberRepository.class);
        fragmentRepository = mock(FragmentRepository.class);
        eventPublisher = mock(ApplicationEventPublisher.class);
        memberPaginationServiceCreator = new MemberPaginationServiceCreator(sequenceRepository, bucketisedMemberRepository,
                fragmentRepository, eventPublisher);
    }

    @Test
    void when_CreatePaginationService_Then_CreateService() {
        MemberPaginationService paginationService = memberPaginationServiceCreator.createPaginationService(VIEW_NAME, VIEW);

        assertThat(paginationService).hasFieldOrPropertyWithValue("viewName", VIEW_NAME)
                .hasFieldOrPropertyWithValue("sequenceRepository", sequenceRepository)
                .hasFieldOrPropertyWithValue("bucketisedMemberRepository", bucketisedMemberRepository)
                .hasFieldOrPropertyWithValue("fragmentRepository", fragmentRepository)
                .hasFieldOrPropertyWithValue("eventPublisher", eventPublisher);
    }

}