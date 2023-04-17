package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.MissingFragmentException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.TreeRelation;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.valueobjects.FragmentPair;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

import java.util.List;
import java.util.Optional;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.GENERIC_TREE_RELATION;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

class ParentUpdaterImplTest {

	private final LdesFragmentRepository ldesFragmentRepository = mock(LdesFragmentRepository.class);
	private ParentUpdater parentUpdater;
	private static final String VIEW = "view";
	private static final ViewName VIEW_NAME = new ViewName("collectionName", VIEW);
	private static final LdesFragment DELETED_CHILD = new LdesFragment(
			VIEW_NAME, List.of(new FragmentPair("key", "value")));
	private static final LdesFragment NON_DELETED_CHILD = new LdesFragment(
			VIEW_NAME, List.of(new FragmentPair("key", "value2")));
	private static LdesFragment PARENT;

	@BeforeEach
	void setUp() {
		PARENT = new LdesFragment(VIEW_NAME, List.of());
		parentUpdater = new ParentUpdaterImpl(ldesFragmentRepository);

	}

	@Test
	void when_ParentIsPointingToDeletedChild_RelationIsRemovedAndNewRelationToNonDeletedChildIsAdded() {
		PARENT.addRelation(new TreeRelation("", DELETED_CHILD.getFragmentId(),
				"", "", GENERIC_TREE_RELATION));
		when(ldesFragmentRepository.retrieveMutableFragment(VIEW,
				List.of())).thenReturn(Optional.of(PARENT));
		when(ldesFragmentRepository.retrieveNonDeletedChildFragment(PARENT.getViewName().getFullName(),
				PARENT.getFragmentPairs())).thenReturn(Optional.of(NON_DELETED_CHILD));

		parentUpdater.updateParent(DELETED_CHILD);

		InOrder inOrder = inOrder(ldesFragmentRepository);
		inOrder.verify(ldesFragmentRepository, times(1)).retrieveMutableFragment(VIEW,
				List.of());
		inOrder.verify(ldesFragmentRepository, times(1)).retrieveNonDeletedChildFragment(VIEW,
				List.of());
		inOrder.verify(ldesFragmentRepository, times(1)).saveFragment(PARENT);
		inOrder.verifyNoMoreInteractions();
		assertEquals(1, PARENT.getRelations().size());
		assertTrue(PARENT.containsRelation(new TreeRelation("", NON_DELETED_CHILD.getFragmentId(),
				"", "", GENERIC_TREE_RELATION)));
	}

	@Test
	void when_ParentDoesNotExist_ExceptionIsThrown() {
		when(ldesFragmentRepository.retrieveMutableFragment(VIEW,
				List.of())).thenReturn(Optional.empty());

		MissingFragmentException missingFragmentException = assertThrows(MissingFragmentException.class,
				() -> parentUpdater.updateParent(DELETED_CHILD));
		assertEquals("No fragment exists with fragment identifier: /view",
				missingFragmentException.getMessage());
	}

	@Test
	void when_ParentIsNotPointingToDeletedChild_ParentRemainsAsIsAndNoNeedToSave() {
		PARENT.addRelation(new TreeRelation("", NON_DELETED_CHILD.getFragmentId(),
				"", "", ""));
		when(ldesFragmentRepository.retrieveMutableFragment(VIEW,
				List.of())).thenReturn(Optional.of(PARENT));

		parentUpdater.updateParent(DELETED_CHILD);

		InOrder inOrder = inOrder(ldesFragmentRepository);
		inOrder.verify(ldesFragmentRepository, times(1)).retrieveMutableFragment(VIEW,
				List.of());
		inOrder.verifyNoMoreInteractions();
	}

}