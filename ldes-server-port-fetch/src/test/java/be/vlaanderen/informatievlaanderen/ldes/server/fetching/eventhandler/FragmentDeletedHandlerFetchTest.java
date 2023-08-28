package be.vlaanderen.informatievlaanderen.ldes.server.fetching.eventhandler;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.fragmentation.FragmentDeletedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.LdesFragmentIdentifier;
import be.vlaanderen.informatievlaanderen.ldes.server.fetching.repository.AllocationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class FragmentDeletedHandlerFetchTest {
    @Mock
    private AllocationRepository allocationRepository;
    @InjectMocks
    private FragmentDeletedHandlerFetch fragmentDeletedHandlerFetch;

    @Test
    void when_FragmentIsDeleted_AllAllocationsRelatedToThatFragmentAreDeleted(){
        String fragmentId = "/mobility-hindrances/view?a=b";
        FragmentDeletedEvent fragmentDeletedEvent = new FragmentDeletedEvent(LdesFragmentIdentifier.fromFragmentId(fragmentId));

        fragmentDeletedHandlerFetch.handleFragmentDeletedEvent(fragmentDeletedEvent);

        verify(allocationRepository).deleteByFragmentId(fragmentId);
    }

}