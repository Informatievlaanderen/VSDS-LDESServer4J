package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.valueobjects.FragmentPair;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.relations.TreeRelationsRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.pagination.services.PageCreator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

import java.util.List;

import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.pagination.constants.PaginationConstants.FIRST_PAGE_NUMBER;
import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.pagination.constants.PaginationConstants.PAGE_NUMBER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class PageCreatorTest {
	private static final String VIEW = "view";
	private PageCreator pageCreator;
	private LdesFragmentRepository ldesFragmentRepository;
	private TreeRelationsRepository treeRelationsRepository;

	@BeforeEach
	void setUp() {
		treeRelationsRepository = mock(TreeRelationsRepository.class);
		ldesFragmentRepository = mock(LdesFragmentRepository.class);
		pageCreator = new PageCreator(
				ldesFragmentRepository);
	}

	@Test
	@DisplayName("Creating First Page Fragment")
	void createFirstFragment() {
		LdesFragment parentFragment = new LdesFragment(VIEW,
				List.of());

		LdesFragment newFragment = pageCreator.createFirstFragment(parentFragment);

		verifyAssertionsOnAttributesOfFragment(newFragment);
		assertTrue(newFragment.getFragmentId().contains("/view?pageNumber=" + FIRST_PAGE_NUMBER));
		verifyNoInteractions(treeRelationsRepository);
	}

	@Test
	@DisplayName("Creating Next Page Fragment")
	void createNextFragmentAndCreateRelations() {
		LdesFragment parentFragment = new LdesFragment(VIEW,
				List.of());
		LdesFragment existingLdesFragment = new LdesFragment(
				VIEW, List.of(new FragmentPair(PAGE_NUMBER,
						"1")));

		LdesFragment newFragment = pageCreator.createNewFragment(existingLdesFragment, parentFragment);

		verifyAssertionsOnAttributesOfFragment(newFragment);
		assertTrue(newFragment.getFragmentId().contains("/view?pageNumber=2"));
		InOrder inOrder = inOrder(ldesFragmentRepository);
		inOrder.verify(ldesFragmentRepository, times(1)).saveFragment(existingLdesFragment);
		inOrder.verify(ldesFragmentRepository, times(1)).saveFragment(newFragment);
		inOrder.verifyNoMoreInteractions();
		assertTrue(existingLdesFragment.isImmutable());
	}

	private void verifyAssertionsOnAttributesOfFragment(LdesFragment ldesFragment) {
		assertEquals("/view?pageNumber",
				ldesFragment.getFragmentId().split("=")[0]);
		assertEquals(VIEW, ldesFragment.getViewName());
		assertTrue(ldesFragment.getValueOfKey(PAGE_NUMBER).isPresent());
	}
}
