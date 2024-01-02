package be.vlaanderen.informatievlaanderen.ldes.server.compaction.domain.entities;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.FragmentPair;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.LdesFragmentIdentifier;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.TreeRelation;
import be.vlaanderen.informatievlaanderen.ldes.server.fetching.entities.CompactionCandidate;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Fragment;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.exceptions.MissingFragmentValueException;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.services.FragmentSorter.sortFragments;

public class CompactedFragment {
	public static final String PAGE_NUMBER_KEY = "pageNumber";
	private final Set<CompactionCandidate> toBeCompactedFragments;

	public CompactedFragment(Set<CompactionCandidate> toBeCompactedFragments) {
		this.toBeCompactedFragments = toBeCompactedFragments;
	}

	public Fragment getFragment() {
		return new Fragment(getLdesFragmentIdentifier(), true,
				getMemberCount(), getOutgoingRelations(), null);
	}

	public Set<String> getImpactedFragmentIds() {
		return getImpactedFragments()
				.map(Fragment::getFragmentIdString)
				.collect(Collectors.toSet());
	}

	public List<LdesFragmentIdentifier> getImpactedFragmentIdentifiers() {
		return getImpactedFragments()
				.map(Fragment::getFragmentId)
				.toList();
	}

	public Fragment getFirstImpactedFragment() {
		return getImpactedFragments().findFirst().orElseThrow();
	}

	private LdesFragmentIdentifier getLdesFragmentIdentifier() {
		var fragments = getImpactedFragments().toList();

		String concatKey = getImpactedFragments()
				.map(this::getPageNumber)
				.collect(Collectors.joining("/"));

		List<FragmentPair> fragmentPairs = new ArrayList<>(fragments.get(0).getFragmentPairs());
		fragmentPairs.remove(fragmentPairs.size() - 1);
		fragmentPairs.add(
				new FragmentPair(PAGE_NUMBER_KEY, concatKey));
		return new LdesFragmentIdentifier(fragments.get(0).getViewName(), fragmentPairs);
	}

	private List<TreeRelation> getOutgoingRelations() {
		return getImpactedFragments()
				.reduce((fragment, fragment2) -> fragment2)
				.map(Fragment::getRelations)
				.orElseThrow();
	}

	private String getPageNumber(Fragment firstFragment) {
		return firstFragment
				.getFragmentId()
				.getValueOfFragmentPairKey(PAGE_NUMBER_KEY)
				.orElseThrow(
						() -> new MissingFragmentValueException(firstFragment.getFragmentIdString(), PAGE_NUMBER_KEY));
	}

	private Stream<Fragment> getImpactedFragments() {
		return sortFragments(toBeCompactedFragments.stream()
				.map(CompactionCandidate::getFragment));
	}

	private Integer getMemberCount() {
		return toBeCompactedFragments.stream().map(CompactionCandidate::getSize).reduce(Integer::sum).orElse(0);
	}
}
