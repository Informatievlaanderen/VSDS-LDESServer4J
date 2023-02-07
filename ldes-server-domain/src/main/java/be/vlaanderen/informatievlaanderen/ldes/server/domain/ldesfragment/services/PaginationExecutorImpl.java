package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.TreeRelation;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.relations.TreeRelationsRepository;

import java.util.Optional;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.NEXT_PAGE_RELATION;
import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.PREVIOUS_PAGE_RELATION;
import static java.util.Optional.ofNullable;

public class PaginationExecutorImpl implements PaginationExecutor {

	private LdesFragment lastFragment;
	private final TreeRelationsRepository treeRelationsRepository;

	public PaginationExecutorImpl(TreeRelationsRepository treeRelationsRepository) {
		this.treeRelationsRepository = treeRelationsRepository;
		this.lastFragment = null;
	}

	public Optional<LdesFragment> getLastFragment() {
		return ofNullable(lastFragment);
	}

	public void setLastFragment(LdesFragment lastFragment) {
		this.lastFragment = lastFragment;
	}

	public void linkFragments(LdesFragment fragment) {
		Optional<LdesFragment> previousFragment = getLastFragment();
		if (previousFragment.isPresent()) {
			fragment.linkPrevFragment(previousFragment.get());
			createPaginationRelationships(previousFragment.get(), fragment);
		}
		setLastFragment(fragment);
	}

	private void createPaginationRelationships(LdesFragment prev, LdesFragment next) {
		treeRelationsRepository.addTreeRelation(prev.getFragmentId(),
				new TreeRelation("", prev.getFragmentId(), "", "", NEXT_PAGE_RELATION));
		treeRelationsRepository.addTreeRelation(next.getFragmentId(),
				new TreeRelation("", next.getFragmentId(), "", "", PREVIOUS_PAGE_RELATION));
	}
}
