package be.vlaanderen.informatievlaanderen.ldes.server.compaction.application.services;

import be.vlaanderen.informatievlaanderen.ldes.server.compaction.domain.entities.ViewCapacity;
import be.vlaanderen.informatievlaanderen.ldes.server.compaction.domain.repository.ViewCollection;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.LdesFragmentIdentifier;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Fragment;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.FragmentRepository;
import io.pyroscope.labels.LabelsSet;
import io.pyroscope.labels.Pyroscope;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.ServerConfig.COMPACTION_CRON_KEY;

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

	@Scheduled(cron = COMPACTION_CRON_KEY)
	public void compactFragments() {
		Pyroscope.LabelsWrapper.run(new LabelsSet("service", "compaction"),
				() -> viewCollection.getAllViewCapacities()
						.parallelStream()
						.forEach(viewCapacity -> getRootFragment(viewCapacity)
								.ifPresent(rootFragment -> {
									PaginationStartingNodeIterator paginationStartingNodeIterator = new PaginationStartingNodeIteratorImpl(
											fragmentRepository, rootFragment);
									while (paginationStartingNodeIterator.hasNext()) {
										Fragment next = paginationStartingNodeIterator.next();
										paginationCompactionService
												.applyCompactionStartingFromNode(next);
									}
								})));
	}

	private Optional<Fragment> getRootFragment(ViewCapacity viewCapacity) {
		return fragmentRepository.retrieveFragment(new LdesFragmentIdentifier(viewCapacity.getViewName(), List.of()));
	}
}
