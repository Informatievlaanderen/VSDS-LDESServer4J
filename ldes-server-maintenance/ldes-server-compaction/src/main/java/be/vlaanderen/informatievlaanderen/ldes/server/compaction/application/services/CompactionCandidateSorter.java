package be.vlaanderen.informatievlaanderen.ldes.server.compaction.application.services;

import be.vlaanderen.informatievlaanderen.ldes.server.compaction.application.valueobjects.CompactedPages;
import be.vlaanderen.informatievlaanderen.ldes.server.compaction.application.valueobjects.CompactionCandidates;
import be.vlaanderen.informatievlaanderen.ldes.server.compaction.application.valueobjects.CompactionPageCapacity;
import be.vlaanderen.informatievlaanderen.ldes.server.fetching.entities.CompactionCandidate;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class CompactionCandidateSorter {

	public List<Set<CompactionCandidate>> getSortedCompactionCandidates(List<CompactionCandidate> candidates,
	                                                                    int capacityPerPage) {
		final CompactionCandidates compactionCandidates = new CompactionCandidates(candidates);
		final CompactionPageCapacity compactionPageCapacity = new CompactionPageCapacity(capacityPerPage);
		return compactionCandidates
				.getLeadingPages().stream()
				.flatMap(leadingPage -> getCompactedCandidatesForLeadingPage(leadingPage, compactionCandidates, compactionPageCapacity).stream())
				.toList();
	}

	private List<Set<CompactionCandidate>> getCompactedCandidatesForLeadingPage(CompactionCandidate leadingPage,
	                                                                            CompactionCandidates candidates,
	                                                                            CompactionPageCapacity capacity) {
		CompactedPages compactedPages = new CompactedPages();
		Optional<CompactionCandidate> currentCandidate = Optional.of(leadingPage);

		while (currentCandidate.isPresent()) {
			addCandidatesToCapacity(capacity, currentCandidate.get(), compactedPages);
			currentCandidate = candidates.getNextCandidate(currentCandidate.get().getNextPageId());
		}

		compactedPages.addCandidatesToCompactedPages();

		return compactedPages.getPages();
	}

	private void addCandidatesToCapacity(CompactionPageCapacity compactionPageCapacity,
	                                     CompactionCandidate candidate,
	                                     CompactedPages compactedPages) {
		compactionPageCapacity.increase(candidate.getSize());
		if (compactionPageCapacity.exceedsMaxCapacity()) {
			compactionPageCapacity.reset();
			compactedPages.addCandidatesToCompactedPages();
		} else {
			compactedPages.addCompactionCandidate(candidate);
		}
	}

}
