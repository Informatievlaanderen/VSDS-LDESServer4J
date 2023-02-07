package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.valueobjects.FragmentPair;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.relations.TreeRelationsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static java.util.Optional.empty;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PaginationExecutorTest {
	private PaginationExecutorImpl paginationExecutor;
	private TreeRelationsRepository treeRelationsRepository;
	private static final String VIEW = "view";
	private static LdesFragment FIRST_FRAGMENT = new LdesFragment(VIEW, List.of());
	private static LdesFragment SECOND_FRAGMENT = new LdesFragment(
			VIEW, List.of(new FragmentPair("key", "value")));

	@BeforeEach
	void setUp() {
		treeRelationsRepository = mock(TreeRelationsRepository.class);
		paginationExecutor = new PaginationExecutorImpl(treeRelationsRepository);
	}

	@Test
	void when_NoPreviousFragment_FragmentIsNotLinked() {
		paginationExecutor.linkFragments(FIRST_FRAGMENT);
		assertEquals(FIRST_FRAGMENT.getPrev(), empty());
		assertEquals(paginationExecutor.getLastFragment().get(), FIRST_FRAGMENT);
		verifyNoInteractions(treeRelationsRepository);
	}

	@Test
	void when_PreviousFragmentExists_FragmentsAreLinked() {
		paginationExecutor.setLastFragment(FIRST_FRAGMENT);
		paginationExecutor.linkFragments(SECOND_FRAGMENT);
		assertEquals(FIRST_FRAGMENT.getNext().get(), SECOND_FRAGMENT);
		assertEquals(SECOND_FRAGMENT.getPrev().get(), FIRST_FRAGMENT);
		verify(treeRelationsRepository, times(2)).addTreeRelation(anyString(), any());
	}
}
