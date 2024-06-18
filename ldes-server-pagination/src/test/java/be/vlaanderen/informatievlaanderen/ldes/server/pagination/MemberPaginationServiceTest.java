package be.vlaanderen.informatievlaanderen.ldes.server.pagination;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.FragmentPair;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.LdesFragmentIdentifier;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.fetching.entities.MemberAllocation;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.BucketisedMember;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Fragment;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.FragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.pagination.constants.PaginationConstants;
import be.vlaanderen.informatievlaanderen.ldes.server.pagination.services.OpenPageProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class MemberPaginationServiceTest {
    private final ViewName VIEW_NAME = new ViewName("collection", "view");
    private final Long SEQ_NR = -1L;
    private final LdesFragmentIdentifier FRAGMENT_ID = new LdesFragmentIdentifier(VIEW_NAME, List.of());
    private final BucketisedMember MEMBER = new BucketisedMember("id", VIEW_NAME,
            FRAGMENT_ID.asDecodedFragmentId(), SEQ_NR);
    private final Fragment FRAGMENT = new Fragment(FRAGMENT_ID);
    private OpenPageProvider openPageProvider;
    private FragmentRepository fragmentRepository;
    private MemberPaginationService paginationService;

    @BeforeEach
    void setUp() {
        openPageProvider = Mockito.mock(OpenPageProvider.class);
        fragmentRepository = mock(FragmentRepository.class);
        paginationService = new MemberPaginationService(fragmentRepository, openPageProvider, 10);
    }

    @Test
    void when_MemberPresent_Then_MemberPaginated() {
        Fragment child = FRAGMENT.createChild(new FragmentPair(PaginationConstants.PAGE_NUMBER, "1"));
        when(openPageProvider.retrieveOpenFragmentOrCreateNewFragment(FRAGMENT_ID))
                .thenReturn(child);

        List<MemberAllocation> memberAllocations = paginationService.paginateMember(List.of(MEMBER));
        assertEquals(1, memberAllocations.size());

        InOrder inOrder = inOrder(fragmentRepository);
        inOrder.verify(fragmentRepository, times(2)).saveFragment(child);
        inOrder.verifyNoMoreInteractions();
    }
}