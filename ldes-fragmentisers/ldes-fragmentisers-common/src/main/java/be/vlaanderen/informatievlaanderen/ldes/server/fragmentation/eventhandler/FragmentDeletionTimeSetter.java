package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.eventhandler;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.ServerConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.compaction.FragmentsCompactedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.LdesFragmentIdentifier;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.FragmentRepository;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;

@Component
public class FragmentDeletionTimeSetter {
	private final FragmentRepository fragmentRepository;
	private final Duration compactionDuration;

	public FragmentDeletionTimeSetter(ServerConfig serverConfig,
			FragmentRepository fragmentRepository) {
		this.fragmentRepository = fragmentRepository;
		this.compactionDuration = Duration.parse(serverConfig.getCompactionDuration());
	}

	@EventListener
	public void handleFragmentsCompactedEvent(FragmentsCompactedEvent event) {
		event.compactedFragments().forEach(this::setDeleteTimeOfFragment);
	}

	private void setDeleteTimeOfFragment(LdesFragmentIdentifier event) {
		fragmentRepository
				.retrieveFragment(event)
				.ifPresent(fragment -> {
					fragment.setDeleteTime(LocalDateTime.now().plus(compactionDuration));
					fragmentRepository.saveFragment(fragment);
				});
	}
}
