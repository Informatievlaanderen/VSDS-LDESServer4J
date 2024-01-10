package be.vlaanderen.informatievlaanderen.ldes.server.fetching.eventhandler;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.fragmentation.BulkFragmentDeletedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.LdesFragmentIdentifier;
import be.vlaanderen.informatievlaanderen.ldes.server.fetching.repository.AllocationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class FragmentDeletedHandlerFetchTest {
	@Mock
	private AllocationRepository allocationRepository;
	@InjectMocks
	private FragmentDeletedHandlerFetch fragmentDeletedHandlerFetch;

	@Test
	void when_FragmentIsDeleted_AllAllocationsRelatedToThatFragmentAreDeleted() {
		String fragmentId = "/mobility-hindrances/view?a=b";
		BulkFragmentDeletedEvent fragmentDeletedEvent = new BulkFragmentDeletedEvent(
				Set.of(LdesFragmentIdentifier.fromFragmentId(fragmentId)));

		fragmentDeletedHandlerFetch.handleBulkFragmentDeletedEvent(fragmentDeletedEvent);

		verify(allocationRepository).deleteAllByFragmentId(Set.of(fragmentId));
	}

}