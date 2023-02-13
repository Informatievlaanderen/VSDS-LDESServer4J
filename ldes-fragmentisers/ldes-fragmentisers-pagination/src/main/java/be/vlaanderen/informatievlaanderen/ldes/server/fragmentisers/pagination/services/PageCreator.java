package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.pagination.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.NonCriticalTasksExecutor;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.TreeRelation;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.valueobjects.FragmentPair;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.relations.TreeRelationsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.GENERIC_TREE_RELATION;
import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.pagination.constants.PaginationConstants.FIRST_PAGE_NUMBER;
import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.pagination.constants.PaginationConstants.PAGE_NUMBER;

public class PageCreator {
	private final LdesFragmentRepository ldesFragmentRepository;
	private final TreeRelationsRepository treeRelationsRepository;
	private final NonCriticalTasksExecutor nonCriticalTasksExecutor;
	private static final Logger LOGGER = LoggerFactory.getLogger(PageCreator.class);

	public PageCreator(LdesFragmentRepository ldesFragmentRepository,
			TreeRelationsRepository treeRelationsRepository,
			NonCriticalTasksExecutor nonCriticalTasksExecutor) {
		this.treeRelationsRepository = treeRelationsRepository;

		this.ldesFragmentRepository = ldesFragmentRepository;
		this.nonCriticalTasksExecutor = nonCriticalTasksExecutor;
	}

	public LdesFragment createFirstFragment(LdesFragment parentFragment) {
		return createFragment(parentFragment, FIRST_PAGE_NUMBER);
	}

	public LdesFragment createNewFragment(LdesFragment previousFragment, LdesFragment parentFragment) {
		String nextPageNumber = getPageNumberAndGiveIncremented(previousFragment);
		LdesFragment newFragment = createFragment(parentFragment, nextPageNumber);
		makeFragmentImmutableAndUpdateRelations(previousFragment, newFragment);
		return newFragment;
	}

	private LdesFragment createFragment(LdesFragment parentFragment, String pageNumber) {
		LdesFragment newFragment = parentFragment.createChild(new FragmentPair(PAGE_NUMBER, pageNumber));
		LOGGER.debug("Pagination fragment created with id: {}", newFragment.getFragmentId());
		return newFragment;
	}

	private String getPageNumberAndGiveIncremented(LdesFragment previousFragment) {
		String previousPageNumber = previousFragment.getValueOfKey(PAGE_NUMBER).orElseThrow();
		int incremented = Integer.parseInt(previousPageNumber) + 1;
		return String.valueOf(incremented);
	}

	private void makeFragmentImmutableAndUpdateRelations(LdesFragment completeLdesFragment,
			LdesFragment newFragment) {
		completeLdesFragment.makeImmutable();
		nonCriticalTasksExecutor
				.submit(() -> treeRelationsRepository.addTreeRelation(newFragment.getFragmentId(),
						new TreeRelation("", completeLdesFragment.getFragmentId(), "", "", GENERIC_TREE_RELATION)));
		ldesFragmentRepository.saveFragment(completeLdesFragment);
		nonCriticalTasksExecutor
				.submit(() -> treeRelationsRepository.addTreeRelation(completeLdesFragment.getFragmentId(),
						new TreeRelation("", newFragment.getFragmentId(), "", "", GENERIC_TREE_RELATION)));
	}
}
