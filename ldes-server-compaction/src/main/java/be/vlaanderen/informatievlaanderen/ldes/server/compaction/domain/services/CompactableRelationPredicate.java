package be.vlaanderen.informatievlaanderen.ldes.server.compaction.domain.services;

import be.vlaanderen.informatievlaanderen.ldes.server.compaction.domain.CompactionCandidate;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.TreeRelation;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Fragment;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.FragmentRepository;

import java.util.List;
import java.util.function.Predicate;

public class CompactableRelationPredicate implements Predicate<CompactionCandidate> {

	private final FragmentRepository fragmentRepository;

	public CompactableRelationPredicate(FragmentRepository fragmentRepository) {
		this.fragmentRepository = fragmentRepository;
	}

	@Override
	public boolean test(CompactionCandidate compactionCandidate) {
		System.out.println("testing relation for " + compactionCandidate.getFirstFragment().getFragmentIdString() + " and " + compactionCandidate.getSecondFragment().getFragmentIdString());
		return firstFragmentHasExactlyOneRelationAndItIsToSecondFragment(compactionCandidate.getFirstFragment(),
				compactionCandidate.getSecondFragment()) &&
				secondFragmentIsReferencedInExactlyOneRelationAndItIsFromFirstFragment(
						compactionCandidate.getFirstFragment(), compactionCandidate.getSecondFragment());
	}

	private boolean firstFragmentHasExactlyOneRelationAndItIsToSecondFragment(Fragment fragment,
			Fragment secondFragment) {
		return fragment.getRelations().size() == 1
				&& fragment.getRelations().get(0).treeNode().equals(secondFragment.getFragmentId());
	}

	private boolean secondFragmentIsReferencedInExactlyOneRelationAndItIsFromFirstFragment(Fragment firstFragment,
			Fragment secondFragment) {
		List<Fragment> fragments = fragmentRepository
				.retrieveFragmentsByOutgoingRelation(secondFragment.getFragmentId());
		return fragments.size() == 1
				&& fragments.get(0).equals(firstFragment)
				&& fragments.get(0).getRelations().stream().map(TreeRelation::treeNode)
						.anyMatch(treeNode -> treeNode.equals(secondFragment.getFragmentId()));

	}
}
