package be.vlaanderen.informatievlaanderen.ldes.server.compaction.domain.entities;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.FragmentPair;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.LdesFragmentIdentifier;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.TreeRelation;
import be.vlaanderen.informatievlaanderen.ldes.server.fetching.entities.CompactionCandidate;
import be.vlaanderen.informatievlaanderen.ldes.server.fetching.entities.TreeNode;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.exceptions.MissingFragmentValueException;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static be.vlaanderen.informatievlaanderen.ldes.server.compaction.application.services.FragmentSorter.sortFragments;

public class CompactedFragment {
	public static final String PAGE_NUMBER_KEY = "pageNumber";
	private final Set<CompactionCandidate> toBeCompactedFragments;

	public CompactedFragment(Set<CompactionCandidate> toBeCompactedFragments) {
		this.toBeCompactedFragments = toBeCompactedFragments;
	}

	public TreeNode getFragment() {
		return new TreeNode("", true, false, getOutgoingRelations(),
				List.of(), ""/*getImpactedFragments().toList().get(0)*/, null);


//		return new Fragment(getLdesFragmentIdentifier(), true,
//				getMemberCount(), getOutgoingRelations(), null);
	}

	public Set<String> getImpactedFragmentIds() {
		return getImpactedFragments()
				.map(TreeNode::getFragmentId)
				.collect(Collectors.toSet());
	}

	public TreeNode getFirstImpactedFragment() {
		return getImpactedFragments().findFirst().orElseThrow();
	}

	private LdesFragmentIdentifier getLdesFragmentIdentifier() {
		var fragments = getImpactedFragments().toList();

		String concatKey = getImpactedFragments()
				.map(this::getPageNumber)
				.collect(Collectors.joining("/"));

		List<FragmentPair> fragmentPairs = new ArrayList<>(LdesFragmentIdentifier.fromFragmentId(fragments.get(0).getFragmentId()).getFragmentPairs());
		fragmentPairs.remove(fragmentPairs.size() - 1);
		fragmentPairs.add(
				new FragmentPair(PAGE_NUMBER_KEY, concatKey));
		return new LdesFragmentIdentifier(""/*fragments.get(0).getViewName()*/, fragmentPairs);
	}

	private List<TreeRelation> getOutgoingRelations() {
		return getImpactedFragments()
				.reduce((fragment, fragment2) -> fragment2)
				.map(TreeNode::getRelations)
				.orElseThrow();
	}

	private String getPageNumber(TreeNode firstFragment) {
		return LdesFragmentIdentifier.fromFragmentId(firstFragment.getFragmentId())
				.getValueOfFragmentPairKey(PAGE_NUMBER_KEY)
				.orElseThrow(
						() -> new MissingFragmentValueException(firstFragment.getFragmentId(), PAGE_NUMBER_KEY));
	}

	private Stream<TreeNode> getImpactedFragments() {
		return sortFragments(toBeCompactedFragments.stream()
				.map(CompactionCandidate::getTreeNode));
	}

	private Integer getMemberCount() {
		return toBeCompactedFragments.stream().map(CompactionCandidate::getSize).reduce(Integer::sum).orElse(0);
	}
}
