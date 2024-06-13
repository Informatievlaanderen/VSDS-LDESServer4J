package be.vlaanderen.informatievlaanderen.ldes.server.pagination;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewSpecification;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.FragmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

class MemberPaginationServiceCreatorTest {
    private final ViewName VIEW_NAME = new ViewName("collection", "view");
    private final ViewSpecification VIEW = new ViewSpecification(VIEW_NAME, List.of(), List.of(), 50);
	private MemberPaginationServiceCreator memberPaginationServiceCreator;

    @BeforeEach
    void setUp() {
        memberPaginationServiceCreator = new MemberPaginationServiceCreator(mock(FragmentRepository.class));
    }

    @Test
    void when_CreatePaginationService_Then_CreateService() {
        MemberPaginationService paginationService = memberPaginationServiceCreator.createPaginationService(VIEW);

        assertEquals(VIEW.getPageSize(), paginationService.getMaxPageSize());
    }

}