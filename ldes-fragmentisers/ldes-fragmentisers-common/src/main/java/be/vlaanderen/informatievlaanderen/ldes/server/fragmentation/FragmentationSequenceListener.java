package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.view.valueobject.ViewDeletedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.FragmentSequenceRepository;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class FragmentationSequenceListener {

	private final FragmentSequenceRepository fragmentSequenceRepository;

	public FragmentationSequenceListener(FragmentSequenceRepository fragmentSequenceRepository) {
		this.fragmentSequenceRepository = fragmentSequenceRepository;
	}

	@EventListener
	public void handleViewDeleted(ViewDeletedEvent event) {
		fragmentSequenceRepository.deleteByViewName(event.getViewName());
	}

}
