package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.MissingFragmentException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.TreeRelation;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.valueobjects.FragmentPair;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.relations.TreeRelationsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

import java.util.List;
import java.util.Optional;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.GENERIC_TREE_RELATION;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class ParentUpdaterImplTest {

	private final LdesFragmentRepository ldesFragmentRepository = mock(LdesFragmentRepository.class);
	private final TreeRelationsRepository treeRelationsRepository = mock(TreeRelationsRepository.class);
	private ParentUpdater parentUpdater;
	private static final String VIEW = "view";
	private static final LdesFragment DELETED_CHILD = new LdesFragment(
			VIEW, List.of(new FragmentPair("key", "value")));
	private static final LdesFragment NON_DELETED_CHILD = new LdesFragment(
			VIEW, List.of(new FragmentPair("key", "value2")));
	private static final LdesFragment PARENT = new LdesFragment(VIEW, List.of());

	@BeforeEach
	void setUp() {
		parentUpdater = new ParentUpdaterImpl(ldesFragmentRepository, treeRelationsRepository);

	}

	@Test
	void when_ParentIsPointingToDeletedChild_RelationIsRemovedAndNewRelationToNonDeletedChildIsAdded() {
		when(ldesFragmentRepository.retrieveMutableFragment(VIEW,
				List.of())).thenReturn(Optional.of(PARENT));
		TreeRelation oldRelation = new TreeRelation("", DELETED_CHILD.getFragmentId(),
				"", "", GENERIC_TREE_RELATION);
		TreeRelation newRelation = new TreeRelation("", NON_DELETED_CHILD.getFragmentId(),
				"", "", GENERIC_TREE_RELATION);
		when(treeRelationsRepository.getRelations(PARENT.getFragmentId())).thenReturn(List.of(oldRelation));
		when(ldesFragmentRepository.retrieveNonDeletedChildFragment(PARENT.getViewName(),
				PARENT.getFragmentPairs())).thenReturn(Optional.of(NON_DELETED_CHILD));

		parentUpdater.updateParent(DELETED_CHILD);

		InOrder inOrder = inOrder(ldesFragmentRepository, treeRelationsRepository);
		inOrder.verify(ldesFragmentRepository, times(1)).retrieveMutableFragment(VIEW,
				List.of());
		inOrder.verify(treeRelationsRepository, times(1)).getRelations(PARENT.getFragmentId());
		inOrder.verify(ldesFragmentRepository, times(1)).retrieveNonDeletedChildFragment(VIEW,
				List.of());
		inOrder.verify(treeRelationsRepository, times(1)).deleteTreeRelation(PARENT.getFragmentId(), oldRelation);
		inOrder.verify(treeRelationsRepository, times(1)).addTreeRelation(PARENT.getFragmentId(), newRelation);
		inOrder.verifyNoMoreInteractions();
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
		when(ldesFragmentRepository.retrieveMutableFragment(VIEW,
				List.of())).thenReturn(Optional.of(PARENT));
		when(treeRelationsRepository.getRelations(PARENT.getFragmentId()))
				.thenReturn(List.of(new TreeRelation("", NON_DELETED_CHILD.getFragmentId(),
						"", "", "")));

		parentUpdater.updateParent(DELETED_CHILD);

		InOrder inOrder = inOrder(ldesFragmentRepository, treeRelationsRepository);
		inOrder.verify(ldesFragmentRepository, times(1)).retrieveMutableFragment(VIEW,
				List.of());
		inOrder.verify(treeRelationsRepository, times(1)).getRelations(PARENT.getFragmentId());
		inOrder.verifyNoMoreInteractions();
	}

}