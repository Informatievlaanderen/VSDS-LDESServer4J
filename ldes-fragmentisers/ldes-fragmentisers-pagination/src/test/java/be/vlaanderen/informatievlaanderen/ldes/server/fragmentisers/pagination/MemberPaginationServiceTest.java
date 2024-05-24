package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.pagination;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.fragmentation.MemberAllocatedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.FragmentPair;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.LdesFragmentIdentifier;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.BucketisedMember;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Fragment;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.BucketisedMemberRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.FragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.FragmentSequenceRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.pagination.services.OpenPageProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;

import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.pagination.constants.PaginationConstants.PAGE_NUMBER;
import static org.mockito.Mockito.*;

class MemberPaginationServiceTest {
    private final ViewName VIEW_NAME = new ViewName("collection", "view");
    private final Long SEQ_NR = -1L;
    private final LdesFragmentIdentifier FRAGMENT_ID = new LdesFragmentIdentifier(VIEW_NAME, List.of());
    private final BucketisedMember MEMBER = new BucketisedMember("id", VIEW_NAME,
            FRAGMENT_ID.asDecodedFragmentId(), SEQ_NR);
    private final Fragment FRAGMENT = new Fragment(FRAGMENT_ID);
    private FragmentSequenceRepository fragmentSequenceRepository;
    private BucketisedMemberRepository bucketisedMemberRepository;
    private OpenPageProvider openPageProvider;
    private FragmentRepository fragmentRepository;
    private ApplicationEventPublisher eventPublisher;
    private MemberPaginationService paginationService;

    @BeforeEach
    void setUp() {
        fragmentSequenceRepository = mock(FragmentSequenceRepository.class);
        bucketisedMemberRepository = mock(BucketisedMemberRepository.class);
        openPageProvider = mock(OpenPageProvider.class);
        fragmentRepository = mock(FragmentRepository.class);
        eventPublisher = mock(ApplicationEventPublisher.class);
        paginationService = new MemberPaginationService(fragmentSequenceRepository, bucketisedMemberRepository,
                openPageProvider, fragmentRepository, eventPublisher, VIEW_NAME);
    }

    @Test
    void when_MemberPresent_Then_MemberPaginated() {
        when(bucketisedMemberRepository.getFirstUnallocatedMember(VIEW_NAME, SEQ_NR))
                .thenReturn(List.of(MEMBER));
        Fragment child = FRAGMENT.createChild(new FragmentPair(PAGE_NUMBER, "1"));
        when(openPageProvider.retrieveOpenFragmentOrCreateNewFragment(FRAGMENT_ID))
                .thenReturn(child);

        paginationService.paginateMember();

        InOrder inOrder = inOrder(eventPublisher, fragmentRepository);
        inOrder.verify(eventPublisher, times(1)).publishEvent(new MemberAllocatedEvent(MEMBER.memberId(), VIEW_NAME.getCollectionName(),
                MEMBER.viewName().asString(), child.getFragmentIdString()));
        inOrder.verify(fragmentRepository, times(1)).incrementNrOfMembersAdded(child.getFragmentId());
        inOrder.verifyNoMoreInteractions();
    }
}