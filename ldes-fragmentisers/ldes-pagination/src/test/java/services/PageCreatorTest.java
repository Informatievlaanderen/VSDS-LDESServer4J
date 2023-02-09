package services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.NonCriticalTasksExecutor;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.valueobjects.FragmentPair;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.relations.TreeRelationsRepository;
import be.vlaanderen.informatievlaanderen.vsds.services.PageCreator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.PAGE_NUMBER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

class PageCreatorTest {
	private static final String VIEW = "view";
	private PageCreator pageCreator;
	private LdesFragmentRepository ldesFragmentRepository;
	private TreeRelationsRepository treeRelationsRepository;
	private NonCriticalTasksExecutor nonCriticalTasksExecutor;

	@BeforeEach
	void setUp() {
		nonCriticalTasksExecutor = new NonCriticalTasksExecutor(
				new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS,
						new LinkedBlockingQueue<Runnable>()));
		treeRelationsRepository = mock(TreeRelationsRepository.class);
		ldesFragmentRepository = mock(LdesFragmentRepository.class);
		pageCreator = new PageCreator(
				ldesFragmentRepository, treeRelationsRepository,
				nonCriticalTasksExecutor);
	}

	@Test
	@DisplayName("Creating First Page Fragment")
	void createFirstFragment() {
		LdesFragment parentFragment = new LdesFragment(VIEW,
				List.of());

		LdesFragment newFragment = pageCreator.createFirstFragment(parentFragment);

		verifyAssertionsOnAttributesOfFragment(newFragment);
		assertTrue(newFragment.getFragmentId().contains("/view?pageNumber=1"));
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
		verify(treeRelationsRepository, times(1)).addTreeRelation(eq(existingLdesFragment.getFragmentId()), any());
		verify(treeRelationsRepository, times(1)).addTreeRelation(eq(newFragment.getFragmentId()), any());
		assertTrue(existingLdesFragment.isImmutable());
	}

	private void verifyAssertionsOnAttributesOfFragment(LdesFragment ldesFragment) {
		assertEquals("/view?pageNumber",
				ldesFragment.getFragmentId().split("=")[0]);
		assertEquals(VIEW, ldesFragment.getViewName());
		assertTrue(ldesFragment.getValueOfKey(PAGE_NUMBER).isPresent());
	}
}
