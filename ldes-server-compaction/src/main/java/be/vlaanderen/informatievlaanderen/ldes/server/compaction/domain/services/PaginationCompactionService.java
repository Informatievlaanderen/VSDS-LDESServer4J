package be.vlaanderen.informatievlaanderen.ldes.server.compaction.domain.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.TreeRelation;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Fragment;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.FragmentRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class PaginationCompactionService {

	private final CompactableFragmentPredicate compactableFragmentPredicate;
	private final CompactableRelationPredicate compactableRelationPredicate;
	private final FragmentRepository fragmentRepository;
	private final FragmentCompactionService fragmentCompactionService;

	public PaginationCompactionService(FragmentRepository fragmentRepository,
			FragmentCompactionService fragmentCompactionService) {
		this.fragmentRepository = fragmentRepository;
		this.fragmentCompactionService = fragmentCompactionService;
		this.compactableFragmentPredicate = new CompactableFragmentPredicate();
		this.compactableRelationPredicate = new CompactableRelationPredicate(this.fragmentRepository);

	}

	public void applyCompactionStartingFromNode(Fragment fragment) {
		Fragment firstFragmentOfCompaction = fragment;
		while (firstFragmentOfCompactionHasOutgoingRelations(firstFragmentOfCompaction)) {
			Optional<Fragment> secondFragmentOfCompaction = retrieveMostCompactedRelationFragment(
					firstFragmentOfCompaction);
			if (secondFragmentOfCompaction.isPresent()) {
				testAndApplyPossibleCompaction(firstFragmentOfCompaction, secondFragmentOfCompaction.get());
				firstFragmentOfCompaction = secondFragmentOfCompaction.get();
			} else {
				break;
			}
		}
	}

	private void testAndApplyPossibleCompaction(Fragment firstFragmentOfCompaction,
			Fragment secondFragmentOfCompaction) {
		if (compactableRelationPredicate.test(firstFragmentOfCompaction)
				&& compactableFragmentPredicate.test(secondFragmentOfCompaction)
				&& compactableFragmentPredicate.test(firstFragmentOfCompaction)) {
			fragmentCompactionService.compactFragments(firstFragmentOfCompaction, secondFragmentOfCompaction);
		}
	}

	private boolean firstFragmentOfCompactionHasOutgoingRelations(Fragment firstFragmentOfCompaction) {
		return !firstFragmentOfCompaction.getRelations().isEmpty();
	}

	private Optional<Fragment> retrieveMostCompactedRelationFragment(Fragment currentFragment) {
		return fragmentRepository
				.retrieveFragment(
						currentFragment
								.getRelations()
								.stream()
								.map(TreeRelation::treeNode)
								.max(new CompactionComparator())
								.orElse(currentFragment
										.getRelations()
										.get(0)
										.treeNode()));
	}
}
