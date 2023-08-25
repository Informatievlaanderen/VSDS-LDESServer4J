package be.vlaanderen.informatievlaanderen.ldes.server.compaction.application.services;

import be.vlaanderen.informatievlaanderen.ldes.server.compaction.domain.entities.ViewCapacity;
import be.vlaanderen.informatievlaanderen.ldes.server.compaction.domain.repository.ViewCollection;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.RootFragmentRetriever;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Fragment;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.FragmentRepository;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class CompactionScheduler {
	private final ViewCollection viewCollection;
	private final FragmentRepository fragmentRepository;
	private final PaginationCompactionService paginationCompactionService;
	private final RootFragmentRetriever rootFragmentRetriever;
	private final ObservationRegistry observationRegistry;

	public CompactionScheduler(ViewCollection viewCollection, FragmentRepository fragmentRepository,
			PaginationCompactionService paginationCompactionService, ObservationRegistry observationRegistry) {
		this.viewCollection = viewCollection;
		this.fragmentRepository = fragmentRepository;
		this.paginationCompactionService = paginationCompactionService;
		this.rootFragmentRetriever = new RootFragmentRetriever(fragmentRepository, observationRegistry);
		this.observationRegistry = observationRegistry;
	}

	@Scheduled(fixedDelay = 10000)
	public void compactFragments() {
		viewCollection.getAllViewCapacities()
				.parallelStream()
				.forEach(viewCapacity -> {
					Fragment rootFragment = getRootFragment(viewCapacity);
					PaginationStartingNodeIterator paginationStartingNodeIterator = new PaginationStartingNodeIteratorImpl(
							fragmentRepository, rootFragment);
					while (paginationStartingNodeIterator.hasNext()) {
						paginationCompactionService
								.applyCompactionStartingFromNode(paginationStartingNodeIterator.next());
					}

				});
	}

	private Fragment getRootFragment(ViewCapacity viewCapacity) {
		Observation compactionObservation = Observation
				.createNotStarted("compaction-retrieve-root", observationRegistry).start();
		Fragment rootFragment = rootFragmentRetriever.retrieveRootFragmentOfView(viewCapacity.getViewName(),
				compactionObservation);
		compactionObservation.stop();
		return rootFragment;
	}
}
