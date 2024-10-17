package be.vlaanderen.informatievlaanderen.ldes.server.compaction.application.valueobjects;

import be.vlaanderen.informatievlaanderen.ldes.server.compaction.domain.entities.CompactionCandidate;

import java.util.List;
import java.util.Optional;

public class CompactionCandidates {
	private final List<CompactionCandidate> candidates;

	public CompactionCandidates(List<CompactionCandidate> candidates) {
		this.candidates = candidates;
	}

	public List<CompactionCandidate> getLeadingPages() {
		return candidates.stream()
				.filter(candidate -> hasNoConnections(candidate, List.copyOf(candidates)))
				.toList();
	}

	public Optional<CompactionCandidate> getNextCandidate(long nextId) {
		return candidates.stream()
				.filter(candidate -> candidate.getId() == nextId)
				.findFirst();
	}

	private static boolean hasNoConnections(CompactionCandidate candidate, List<CompactionCandidate> pages) {
		return pages.stream().noneMatch(page -> isConnectedTo(page, candidate));
	}

	private static boolean isConnectedTo(CompactionCandidate treeNode, CompactionCandidate otherTreeNode) {
		return treeNode.getNextPageId() == otherTreeNode.getId();
	}
}
