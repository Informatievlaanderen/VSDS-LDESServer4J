package be.vlaanderen.informatievlaanderen.ldes.server.fetching.eventhandler;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.retention.MemberUnallocatedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.fetching.repository.AllocationRepository;
import io.micrometer.core.instrument.Metrics;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class MemberUnallocatedHandlerFetch {

	private static final String LDES_SERVER_MEMBERS_REMOVED_FROM_FRAGMENT_COUNT =
			"ldes_server_members_removed_from_fragment_count";

	private final AllocationRepository allocationRepository;

	public MemberUnallocatedHandlerFetch(AllocationRepository allocationRepository) {
		this.allocationRepository = allocationRepository;
	}

	@EventListener
	public void handleMemberUnallocatedEvent(MemberUnallocatedEvent event) {
		allocationRepository.deleteByMemberIdAndCollectionNameAndViewName(event.memberId(),
				event.viewName().getCollectionName(), event.viewName().asString());
		String viewName = event.viewName().asString();
		Metrics.counter(LDES_SERVER_MEMBERS_REMOVED_FROM_FRAGMENT_COUNT, "view",  viewName).increment();
	}
}
