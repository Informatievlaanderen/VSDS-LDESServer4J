package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.EventStreamClosedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.FragmentRepository;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
public class EventStreamClosedEventHandler {
	private final FragmentRepository fragmentRepository;

	public EventStreamClosedEventHandler(FragmentRepository fragmentRepository) {
		this.fragmentRepository = fragmentRepository;
	}

	@EventListener
	public void onEventStreamClosed(EventStreamClosedEvent event) {
		fragmentRepository.markFragmentsImmutableInCollection(event.collectionName());
	}
}
