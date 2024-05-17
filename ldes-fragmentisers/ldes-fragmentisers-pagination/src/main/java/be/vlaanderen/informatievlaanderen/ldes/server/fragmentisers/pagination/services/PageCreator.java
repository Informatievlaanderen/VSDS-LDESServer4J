package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.pagination.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.FragmentPair;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.TreeRelation;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Fragment;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.FragmentRepository;
import io.micrometer.core.instrument.Metrics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.GENERIC_TREE_RELATION;
import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.FragmentationService.LDES_SERVER_CREATE_FRAGMENTS_COUNT;
import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.metrics.MetricsConstants.FRAGMENTATION_STRATEGY;
import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.metrics.MetricsConstants.VIEW;
import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.pagination.constants.PaginationConstants.FIRST_PAGE_NUMBER;
import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.pagination.constants.PaginationConstants.PAGE_NUMBER;

public class PageCreator {
	public static final String PAGINATION_FRAGMENTATION = "PaginationFragmentation";
	private final FragmentRepository fragmentRepository;
	private final boolean bidirectionalRelations;
	private static final Logger LOGGER = LoggerFactory.getLogger(PageCreator.class);

	public PageCreator(FragmentRepository fragmentRepository, boolean bidirectionalRelations) {

		this.fragmentRepository = fragmentRepository;
		this.bidirectionalRelations = bidirectionalRelations;
	}

	public Fragment createFirstFragment(Fragment parentFragment) {
		return createFragment(parentFragment, FIRST_PAGE_NUMBER);
	}

	public Fragment createNewFragment(Fragment previousFragment, Fragment parentFragment) {
		String nextPageNumber = getPageNumberAndGiveIncremented(previousFragment);
		Fragment newFragment = createFragment(parentFragment, nextPageNumber);
		makeFragmentImmutableAndUpdateRelations(previousFragment, newFragment);
		fragmentRepository.saveFragment(newFragment);
		return newFragment;
	}

	private Fragment createFragment(Fragment parentFragment, String pageNumber) {
		Fragment newFragment = parentFragment.createChild(new FragmentPair(PAGE_NUMBER, pageNumber));
		String viewName = parentFragment.getViewName().asString();
		Metrics.counter(LDES_SERVER_CREATE_FRAGMENTS_COUNT, VIEW, viewName, FRAGMENTATION_STRATEGY, PAGINATION_FRAGMENTATION).increment();
		LOGGER.debug("Page created with id: {}", newFragment.getFragmentId());
		return newFragment;
	}

	private String getPageNumberAndGiveIncremented(Fragment previousFragment) {
		String previousPageNumber = previousFragment.getValueOfKey(PAGE_NUMBER).orElseThrow();
		int incremented = Integer.parseInt(previousPageNumber) + 1;
		return String.valueOf(incremented);
	}

	private void makeFragmentImmutableAndUpdateRelations(Fragment completeFragment,
			Fragment newFragment) {
		completeFragment.makeImmutable();
		completeFragment
				.addRelation(new TreeRelation("", newFragment.getFragmentId(), "", "", GENERIC_TREE_RELATION));
		if (bidirectionalRelations) {
			newFragment
					.addRelation(
							new TreeRelation("", completeFragment.getFragmentId(), "", "", GENERIC_TREE_RELATION));
		}
		fragmentRepository.saveFragment(completeFragment);
	}
}
