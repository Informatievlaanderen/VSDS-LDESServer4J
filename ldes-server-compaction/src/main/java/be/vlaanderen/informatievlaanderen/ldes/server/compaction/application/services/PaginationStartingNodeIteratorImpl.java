package be.vlaanderen.informatievlaanderen.ldes.server.compaction.application.services;

import be.vlaanderen.informatievlaanderen.ldes.server.compaction.domain.services.CompactableFragmentPredicate;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Fragment;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.FragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.TreeRelation;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PaginationStartingNodeIteratorImpl implements PaginationStartingNodeIterator {
	private final FragmentRepository fragmentRepository;
	private final CompactableFragmentPredicate compactableFragmentPredicate;
	private final List<Fragment> structuralFragments;
	private final List<Fragment> startingNodeFragments;

	public PaginationStartingNodeIteratorImpl(FragmentRepository fragmentRepository, Fragment rootFragment) {
		this.fragmentRepository = fragmentRepository;
		this.compactableFragmentPredicate = new CompactableFragmentPredicate();
		this.structuralFragments = new ArrayList<>();
		this.structuralFragments.add(rootFragment);
		this.startingNodeFragments = new ArrayList<>();
	}

	@Override
	public Fragment next() {
		return startingNodeFragments.remove(0);
	}

	@Override
	public boolean hasNext() {
		while (startingNodeFragments.isEmpty() && !structuralFragments.isEmpty()) {
			processStructuralFragment(structuralFragments.remove(0));
		}
		return !startingNodeFragments.isEmpty();
	}

	private void processStructuralFragment(Fragment fragment) {
		fragment.getRelations()
				.stream()
				.map(TreeRelation::treeNode)
				.map(fragmentRepository::retrieveFragment)
				.flatMap(Optional::stream)
				.forEach(relationFragment -> {
					if (compactableFragmentPredicate.test(relationFragment)) {
						startingNodeFragments.add(relationFragment);
					} else {
						structuralFragments.add(relationFragment);
					}
				});
	}
}
