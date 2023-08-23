package be.vlaanderen.informatievlaanderen.ldes.server.compaction.domain.services;

import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Fragment;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.FragmentRepository;

import java.util.function.Predicate;

public class CompactableRelationPredicate implements Predicate<Fragment> {

	private final FragmentRepository fragmentRepository;

	public CompactableRelationPredicate(FragmentRepository fragmentRepository) {
		this.fragmentRepository = fragmentRepository;
	}

	@Override
	public boolean test(Fragment fragment) {
		return firstFragmentHasOneOutGoingRelation(fragment) &&
				secondFragmentHasOneIncomingRelation(fragment);
	}

	private boolean secondFragmentHasOneIncomingRelation(Fragment fragment) {
		return fragmentRepository.retrieveFragmentsByOutgoingRelation(fragment.getRelations().get(0).treeNode())
				.size() == 1;
	}

	private boolean firstFragmentHasOneOutGoingRelation(Fragment fragment) {
		return fragment.getRelations().size() == 1;
	}
}
