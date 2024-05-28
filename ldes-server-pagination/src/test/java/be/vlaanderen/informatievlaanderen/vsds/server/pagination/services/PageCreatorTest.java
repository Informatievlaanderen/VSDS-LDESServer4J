package be.vlaanderen.informatievlaanderen.vsds.server.pagination.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.FragmentPair;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.LdesFragmentIdentifier;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Fragment;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.FragmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

import java.util.List;

import static be.vlaanderen.informatievlaanderen.vsds.server.pagination.constants.PaginationConstants.FIRST_PAGE_NUMBER;
import static be.vlaanderen.informatievlaanderen.vsds.server.pagination.constants.PaginationConstants.PAGE_NUMBER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class PageCreatorTest {
	private static final ViewName VIEW_NAME = new ViewName("collectionName", "view");
	private PageCreator pageCreator;
	private FragmentRepository fragmentRepository;

	@BeforeEach
	void setUp() {
		fragmentRepository = mock(FragmentRepository.class);
		pageCreator = new PageCreator(
				fragmentRepository, true);
	}

	@Test
	@DisplayName("Creating First Page Fragment")
	void createFirstFragment() {
		Fragment parentFragment = new Fragment(new LdesFragmentIdentifier(VIEW_NAME,
				List.of()));

		Fragment newFragment = pageCreator.createFirstFragment(parentFragment);

		verifyAssertionsOnAttributesOfFragment(newFragment);
		assertTrue(newFragment.getFragmentPairs().contains(new FragmentPair("pageNumber", FIRST_PAGE_NUMBER)));
	}

	@Test
	@DisplayName("Creating Next Page Fragment")
	void createNextFragmentAndCreateRelations() {
		Fragment parentFragment = new Fragment(new LdesFragmentIdentifier(VIEW_NAME,
				List.of()));
		Fragment existingFragment = new Fragment(new LdesFragmentIdentifier(
				VIEW_NAME, List.of(new FragmentPair(PAGE_NUMBER,
						"1"))));

		Fragment newFragment = pageCreator.createNewFragment(existingFragment, parentFragment);

		verifyAssertionsOnAttributesOfFragment(newFragment);
		assertTrue(newFragment.getFragmentPairs().contains(new FragmentPair("pageNumber", "2")));
		InOrder inOrder = inOrder(fragmentRepository);
		inOrder.verify(fragmentRepository, times(1)).saveFragment(existingFragment);
		inOrder.verify(fragmentRepository, times(1)).saveFragment(newFragment);
		inOrder.verifyNoMoreInteractions();
		assertTrue(existingFragment.isImmutable());
	}

	private void verifyAssertionsOnAttributesOfFragment(Fragment fragment) {
		assertEquals("/collectionName/view?pageNumber",
				fragment.getFragmentIdString().split("=")[0]);
		assertEquals(VIEW_NAME, fragment.getViewName());
		assertTrue(fragment.getValueOfKey(PAGE_NUMBER).isPresent());
	}
}
