package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.eventhandler;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.compaction.FragmentsCompactedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.LdesFragmentIdentifier;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.FragmentRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.ServerConstants.COMPACTION_DURATION;

@Component
public class FragmentDeleteTimeSetter {
	private final FragmentRepository fragmentRepository;
	private final Duration compactionDuration;

	public FragmentDeleteTimeSetter(@Value(COMPACTION_DURATION) String compactionDuration,
			FragmentRepository fragmentRepository) {
		this.fragmentRepository = fragmentRepository;
		this.compactionDuration = Duration.parse(compactionDuration);
	}

	@EventListener
	public void handleFragmentsCompactedEvent(FragmentsCompactedEvent event) {
		setDeleteTimeOfFragment(event.firstFragment());
		setDeleteTimeOfFragment(event.secondFragment());
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
