package be.vlaanderen.informatievlaanderen.ldes.server.compaction.domain.entities;

import be.vlaanderen.informatievlaanderen.ldes.server.compaction.domain.services.CompactionCandidateComparator;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.FragmentPair;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.LdesFragmentIdentifier;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.TreeRelation;
import be.vlaanderen.informatievlaanderen.ldes.server.fetching.entities.AllocationAggregate;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.FragmentComparator;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Fragment;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.exceptions.MissingFragmentValueException;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CompactedFragment {
	public static final String PAGE_NUMBER_KEY = "pageNumber";
	private final Set<AllocationAggregate> toBeCompactedFragments;

	public CompactedFragment(Set<AllocationAggregate> toBeCompactedFragments) {
		this.toBeCompactedFragments = toBeCompactedFragments;
	}

	public Fragment getFragment() {
		return new Fragment(getLdesFragmentIdentifier(), true, 0,
				getOutgoingRelations(), null);
	}

	public LdesFragmentIdentifier getLdesFragmentIdentifier() {
		var fragments = getImpactedFragments().toList();

		String concatKey = getImpactedFragments()
				.sorted(new FragmentComparator())
				.map(this::getPageNumber)
				.collect(Collectors.joining("/"));

		List<FragmentPair> fragmentPairs = new ArrayList<>(fragments.get(0).getFragmentPairs());
		fragmentPairs.remove(fragmentPairs.size() - 1);
		fragmentPairs.add(
				new FragmentPair(PAGE_NUMBER_KEY, concatKey));
		return new LdesFragmentIdentifier(fragments.get(0).getViewName(), fragmentPairs);
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

	private List<TreeRelation> getOutgoingRelations() {
		return toBeCompactedFragments.stream()
				.max(new CompactionCandidateComparator())
				.map(AllocationAggregate::getFragment)
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
		return toBeCompactedFragments.stream()
				.sorted(new CompactionCandidateComparator())
				.map(AllocationAggregate::getFragment)
				.sorted(new FragmentComparator());
	}
}
