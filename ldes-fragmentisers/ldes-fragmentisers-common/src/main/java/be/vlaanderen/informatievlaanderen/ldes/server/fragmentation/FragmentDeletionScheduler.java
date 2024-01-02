package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.fragmentation.BulkFragmentDeletedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Fragment;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.FragmentRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.ServerConfig.DELETION_CRON_KEY;

@Service
public class FragmentDeletionScheduler {
	private final FragmentRepository fragmentRepository;
	private final ApplicationEventPublisher applicationEventPublisher;

	public FragmentDeletionScheduler(FragmentRepository fragmentRepository,
	                                 ApplicationEventPublisher applicationEventPublisher) {
		this.fragmentRepository = fragmentRepository;
		this.applicationEventPublisher = applicationEventPublisher;
	}

	@Scheduled(cron = DELETION_CRON_KEY)
	public void deleteFragments() {
		var deletedFragments = fragmentRepository
				.getDeletionCandidates()
				.filter(Fragment::isReadyForDeletion)
				.map(Fragment::getFragmentId)
				.collect(Collectors.toSet());

		deletedFragments.forEach(fragmentRepository::removeRelationsPointingToFragmentAndDeleteFragment);

		applicationEventPublisher.publishEvent(new BulkFragmentDeletedEvent(deletedFragments));
	}

}
