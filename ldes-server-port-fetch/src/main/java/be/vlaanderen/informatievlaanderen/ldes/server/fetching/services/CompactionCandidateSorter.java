package be.vlaanderen.informatievlaanderen.ldes.server.fetching.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.LdesFragmentIdentifier;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.TreeRelation;
import be.vlaanderen.informatievlaanderen.ldes.server.fetching.entities.CompactionCandidate;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.services.FragmentSorter.hasNoConnections;

public class CompactionCandidateSorter {
	private CompactionCandidateSorter() {}
	public static Stream<CompactionCandidate> sortCompactionCandidates(Stream<CompactionCandidate> compactionCandidates) {
		List<CompactionCandidate> candidatesList = compactionCandidates.toList();

		var firstElement = candidatesList.stream()
				.filter(candidate -> hasNoConnections(candidate.getFragment(), candidatesList.stream()
						.map(CompactionCandidate::getFragment)
						.toList()))
				.findFirst()
				.orElseThrow();

		var map = candidatesList.stream()
				.filter(candidate -> !candidate.getFragment().getRelations().isEmpty())
				.collect(Collectors.toMap(CompactionCandidate::getId, candidate -> candidate.getFragment().getRelations()
						.stream()
						.findFirst()
						.map(TreeRelation::treeNode)
						.map(LdesFragmentIdentifier::asDecodedFragmentId)
						.orElseThrow()));

		List<CompactionCandidate> orderedCandidates = new LinkedList<>(List.of(firstElement));
		String currentFragment = firstElement.getId();

		Optional<CompactionCandidate> foundFragment;

		do {
			String fragmentId = map.get(currentFragment);

			foundFragment = candidatesList.stream()
					.filter(candidate -> candidate.getId().equals(fragmentId))
					.findFirst();

			if (foundFragment.isPresent()) {
				orderedCandidates.add(foundFragment.get());
				currentFragment = fragmentId;
			}
		} while (foundFragment.isPresent());

		if(orderedCandidates.size() < candidatesList.size()) {
			throw new IllegalArgumentException("Not all compaction candidates are linked. Candidates: " + candidatesList);
		}

		return orderedCandidates.stream();
	}
}
