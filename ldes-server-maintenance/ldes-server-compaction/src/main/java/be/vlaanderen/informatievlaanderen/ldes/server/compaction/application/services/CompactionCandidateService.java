package be.vlaanderen.informatievlaanderen.ldes.server.compaction.application.services;

import be.vlaanderen.informatievlaanderen.ldes.server.compaction.domain.entities.ViewCapacity;
import be.vlaanderen.informatievlaanderen.ldes.server.fetching.entities.CompactionCandidate;
import be.vlaanderen.informatievlaanderen.ldes.server.pagination.repositories.PageRepository;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import static be.vlaanderen.informatievlaanderen.ldes.server.compaction.application.services.CompactionCandidateSorter.getCompactionCandidateList;

@Component
public class CompactionCandidateService {
	private final PageRepository pageRepository;


	public CompactionCandidateService(PageRepository pageRepository) {
        this.pageRepository = pageRepository;
    }

	/**
	 * Preparation step for the Compaction Service.
	 * Will retrieve an aggregated view of the allocation table where the size of members is lower than the
	 * maximum view capacity.
	 * If these fragments are connected, these will be returned in the same group.
	 *
	 * @param viewCapacity Contains the name and capacity for a view
	 * @return a structured group of Fragments for a view that can be compacted
	 */
	public Collection<Set<CompactionCandidate>> getCompactionTaskList(ViewCapacity viewCapacity) {
		List<CompactionCandidate> possibleCompactionCandidates = getPossibleCompactionCandidates(viewCapacity);

		if (possibleCompactionCandidates.isEmpty()) {
			return List.of();
		}

		return getCompactionCandidateList(possibleCompactionCandidates, viewCapacity.getCapacityPerPage());
	}

	protected List<CompactionCandidate> getPossibleCompactionCandidates(ViewCapacity viewCapacity) {
		var compactionCandidates = pageRepository.getPossibleCompactionCandidates(viewCapacity.getViewName(), viewCapacity.getCapacityPerPage())
				.toList();
		return compactionCandidates.stream()
				.filter(CompactionCandidate::isCompactable)
				.toList();
	}
}
