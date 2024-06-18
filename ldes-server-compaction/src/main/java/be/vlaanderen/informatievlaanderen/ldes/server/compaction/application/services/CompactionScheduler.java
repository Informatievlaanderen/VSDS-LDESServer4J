package be.vlaanderen.informatievlaanderen.ldes.server.compaction.application.services;

import be.vlaanderen.informatievlaanderen.ldes.server.compaction.domain.entities.ViewCapacity;
import be.vlaanderen.informatievlaanderen.ldes.server.compaction.domain.repository.ViewCollection;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.LdesFragmentIdentifier;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Fragment;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.FragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.spi.RetentionPolicyEmptinessChecker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.ServerConfig.COMPACTION_CRON_KEY;

@Service
public class CompactionScheduler {
	private static final Logger LOGGER = LoggerFactory.getLogger(CompactionScheduler.class);
	private final ViewCollection viewCollection;
	private final FragmentRepository fragmentRepository;
	private final PaginationCompactionService paginationCompactionService;
	private final CompactionCandidateService compactionCandidateService;
	private final RetentionPolicyEmptinessChecker retentionPolicyEmptinessChecker;

	public CompactionScheduler(ViewCollection viewCollection, FragmentRepository fragmentRepository,
	                           PaginationCompactionService paginationCompactionService,
	                           CompactionCandidateService compactionCandidateService,
	                           RetentionPolicyEmptinessChecker retentionPolicyEmptinessChecker) {
		this.viewCollection = viewCollection;
		this.fragmentRepository = fragmentRepository;
		this.paginationCompactionService = paginationCompactionService;
		this.compactionCandidateService = compactionCandidateService;
		this.retentionPolicyEmptinessChecker = retentionPolicyEmptinessChecker;
	}

	@SuppressWarnings("java:S6857")
	@Scheduled(cron = COMPACTION_CRON_KEY)
	public void compactFragments() {
		if (retentionPolicyEmptinessChecker.isEmpty()) {
			LOGGER.info("Compaction skipped: no retention policies found.");
			return;
		}
		viewCollection.getAllViewCapacities()
				.parallelStream()
				.forEach(viewCapacity -> getRootFragment(viewCapacity).ifPresent(rootFragment -> {

					var compactionTaskList = compactionCandidateService.getCompactionTaskList(viewCapacity);

					if (compactionTaskList.isEmpty()) {
						LOGGER.info("No compaction candidates available for {}", viewCapacity.getViewName().getViewName());
					} else {
						LOGGER.info("Processing {} compaction candidates available for {}", compactionTaskList.size(), viewCapacity.getViewName().getViewName());

						compactionTaskList.forEach(paginationCompactionService::applyCompactionForFragments);
					}
				}));

	}

	private Optional<Fragment> getRootFragment(ViewCapacity viewCapacity) {
		return fragmentRepository.retrieveFragment(new LdesFragmentIdentifier(viewCapacity.getViewName(), List.of()));
	}

}
