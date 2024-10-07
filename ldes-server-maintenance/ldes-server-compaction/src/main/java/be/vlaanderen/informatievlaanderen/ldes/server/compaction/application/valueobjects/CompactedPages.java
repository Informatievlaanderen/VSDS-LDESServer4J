package be.vlaanderen.informatievlaanderen.ldes.server.compaction.application.valueobjects;

import be.vlaanderen.informatievlaanderen.ldes.server.fetching.entities.CompactionCandidate;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CompactedPages {
	private final List<Set<CompactionCandidate>> pages;
	private final Set<CompactionCandidate> compactedCandidatesToAdd;


	public CompactedPages() {
		pages = new ArrayList<>();
		compactedCandidatesToAdd = new HashSet<>();
	}

	public void closeCompactedPage() {
		if (compactedCandidatesToAdd.size() > 1) {
			pages.add(Set.copyOf(compactedCandidatesToAdd));
			compactedCandidatesToAdd.clear();
		}
	}

	public void addCompactionCandidate(CompactionCandidate candidate) {
		compactedCandidatesToAdd.add(candidate);
	}

	public List<Set<CompactionCandidate>> getPages() {
		return List.copyOf(pages);
	}
}
