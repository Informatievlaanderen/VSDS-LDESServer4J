package be.vlaanderen.informatievlaanderen.ldes.server.fetching.eventhandler;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.fragmentation.BulkFragmentDeletedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.LdesFragmentIdentifier;
import be.vlaanderen.informatievlaanderen.ldes.server.fetching.repository.AllocationRepository;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class FragmentDeletedHandlerFetch {

	private final AllocationRepository allocationRepository;

	public FragmentDeletedHandlerFetch(AllocationRepository allocationRepository) {
		this.allocationRepository = allocationRepository;
	}

	@EventListener
	public void handleBulkFragmentDeletedEvent(BulkFragmentDeletedEvent event) {
		Set<String> fragmentIds = event.ldesFragmentIdentifiers()
				.stream()
				.map(LdesFragmentIdentifier::asDecodedFragmentId)
				.collect(Collectors.toSet());
		allocationRepository.deleteAllByFragmentId(fragmentIds);
	}

}
