package be.vlaanderen.informatievlaanderen.ldes.server.compaction.application.services;

import be.vlaanderen.informatievlaanderen.ldes.server.compaction.domain.repository.ViewCollection;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Fragment;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.FragmentRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CompactionScheduler {
	private final ViewCollection viewCollection;
	private final FragmentRepository fragmentRepository;
	private final PaginationCompactionService paginationCompactionService;

	public CompactionScheduler(ViewCollection viewCollection, FragmentRepository fragmentRepository,
			PaginationCompactionService paginationCompactionService) {
		this.viewCollection = viewCollection;
		this.fragmentRepository = fragmentRepository;
		this.paginationCompactionService = paginationCompactionService;
	}

	@Scheduled(fixedDelay = 10000)
	public void compactFragments() {
		viewCollection.getAllViewCapacities()
				.forEach(viewCapacity -> {
					Optional<Fragment> rootFragment = fragmentRepository
							.retrieveRootFragment(viewCapacity.getViewName().asString());
					if (rootFragment.isPresent()) {
						PaginationStartingNodeIterator paginationStartingNodeIterator = new PaginationStartingNodeIteratorImpl(
								fragmentRepository, rootFragment.get());
						while (paginationStartingNodeIterator.hasNext()) {
							paginationCompactionService
									.applyCompactionStartingFromNode(paginationStartingNodeIterator.next());
						}
					}
				});
	}
}
