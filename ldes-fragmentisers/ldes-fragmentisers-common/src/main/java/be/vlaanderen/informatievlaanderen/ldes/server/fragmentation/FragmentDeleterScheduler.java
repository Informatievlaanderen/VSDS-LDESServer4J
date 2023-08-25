package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.fragmentation.FragmentDeletedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Fragment;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.FragmentRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class FragmentDeleterScheduler {
	private final FragmentRepository fragmentRepository;
	private final ApplicationEventPublisher applicationEventPublisher;

	public FragmentDeleterScheduler(FragmentRepository fragmentRepository,
			ApplicationEventPublisher applicationEventPublisher) {
		this.fragmentRepository = fragmentRepository;
		this.applicationEventPublisher = applicationEventPublisher;
	}

	@Scheduled(fixedDelay = 10000)
	public void deleteFragments() {
		fragmentRepository
				.getDeletionCandidates()
				.filter(Fragment::isReadyForDeletion)
				.forEach(readyForDeletionFragment -> {
					fragmentRepository.deleteFragmentAndRemoveRelationsPointingToFragment(readyForDeletionFragment);
					applicationEventPublisher
							.publishEvent(new FragmentDeletedEvent(readyForDeletionFragment.getFragmentId()));
				});
	}

}
