package be.vlaanderen.informatievlaanderen.ldes.server.compaction.application.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.fragmentation.FragmentDeletedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.TreeRelation;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Fragment;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.FragmentRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

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
					fragmentRepository.deleteFragment(readyForDeletionFragment);
					removeRelationsPointingToDeletedFragment(readyForDeletionFragment);
					applicationEventPublisher
							.publishEvent(new FragmentDeletedEvent(readyForDeletionFragment.getFragmentId()));
				});
	}

	private void removeRelationsPointingToDeletedFragment(Fragment readyForDeletionFragment) {
		List<Fragment> fragments = fragmentRepository
				.retrieveFragmentsByOutgoingRelation(readyForDeletionFragment.getFragmentId());
		fragments.forEach(fragment -> {
			List<TreeRelation> relationsToRemove = fragment.getRelations().stream()
					.filter(treeRelation -> treeRelation.treeNode().equals(readyForDeletionFragment.getFragmentId()))
					.toList();
			relationsToRemove.forEach(fragment::removeRelation);
			fragmentRepository.saveFragment(fragment);
		});
	}
}
