package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.fragmentation.BulkFragmentDeletedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Fragment;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.FragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.repositories.RetentionPolicyCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.ServerConfig.DELETION_CRON_KEY;

@Service
public class FragmentDeletionScheduler {
	private static final Logger LOGGER = LoggerFactory.getLogger(FragmentDeletionScheduler.class);
	private final FragmentRepository fragmentRepository;
	private final ApplicationEventPublisher applicationEventPublisher;
	private final RetentionPolicyCollection retentionPolicyCollection;

	public FragmentDeletionScheduler(FragmentRepository fragmentRepository,
									 ApplicationEventPublisher applicationEventPublisher, RetentionPolicyCollection retentionPolicyCollection) {
		this.fragmentRepository = fragmentRepository;
		this.applicationEventPublisher = applicationEventPublisher;
		this.retentionPolicyCollection = retentionPolicyCollection;
	}

	@Scheduled(cron = DELETION_CRON_KEY)
	public void deleteFragments() {
		if(retentionPolicyCollection.isEmpty()) {
			LOGGER.atDebug().log("Fragment deletion skipped: no retention policies found.");
			return;
		}
		var deletedFragments = fragmentRepository
				.getDeletionCandidates()
				.filter(Fragment::isReadyForDeletion)
				.map(Fragment::getFragmentId)
				.collect(Collectors.toSet());

		deletedFragments.forEach(fragmentRepository::removeRelationsPointingToFragmentAndDeleteFragment);

		applicationEventPublisher.publishEvent(new BulkFragmentDeletedEvent(deletedFragments));
	}

}
