package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.MissingFragmentException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.FragmentInfo;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.TreeRelation;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.valueobjects.FragmentPair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class ParentUpdaterImplTest {

	private final LdesFragmentRepository ldesFragmentRepository = mock(LdesFragmentRepository.class);
	private ParentUpdater parentUpdater;
	private static final String VIEW = "view";
	private static final LdesFragment DELETED_CHILD = new LdesFragment(
			new FragmentInfo(VIEW, List.of(new FragmentPair("key", "value"))));
	private static final LdesFragment NON_DELETED_CHILD = new LdesFragment(
			new FragmentInfo(VIEW, List.of(new FragmentPair("key", "value2"))));
	private static final LdesFragment PARENT = new LdesFragment(new FragmentInfo(VIEW, List.of()));

	@BeforeEach
	void setUp() {
		parentUpdater = new ParentUpdaterImpl(ldesFragmentRepository);

	}

	@Test
	void when_ParentIsPointingToDeletedChild_RelationIsRemovedAndNewRelationToNonDeletedChildIsAdded() {
		PARENT.addRelation(new TreeRelation("", DELETED_CHILD.getFragmentId(), "", "", ""));
		when(ldesFragmentRepository.retrieveMutableFragment(VIEW, List.of())).thenReturn(Optional.of(PARENT));
		when(ldesFragmentRepository.retrieveNonDeletedChildFragment(VIEW, List.of()))
				.thenReturn(Optional.of(NON_DELETED_CHILD));

		parentUpdater.updateParent(DELETED_CHILD);

		verify(ldesFragmentRepository, times(1)).saveFragment(PARENT);
		assertEquals(NON_DELETED_CHILD.getFragmentId(), PARENT.getRelations().get(0).treeNode());
	}

	@Test
	void when_ParentDoesNotExist_ExceptionIsThrown() {
		when(ldesFragmentRepository.retrieveMutableFragment(VIEW, List.of())).thenReturn(Optional.empty());

		MissingFragmentException missingFragmentException = assertThrows(MissingFragmentException.class,
				() -> parentUpdater.updateParent(DELETED_CHILD));
		assertEquals("No fragment exists with fragment identifier: /view", missingFragmentException.getMessage());
	}

	@Test
	void when_ParentIsNotPointingToDeletedChild_ParentRemainsAsIsAndNoNeedToSave() {
		PARENT.addRelation(new TreeRelation("", NON_DELETED_CHILD.getFragmentId(), "", "", ""));
		when(ldesFragmentRepository.retrieveMutableFragment(VIEW, List.of())).thenReturn(Optional.of(PARENT));

		parentUpdater.updateParent(DELETED_CHILD);

		assertEquals(NON_DELETED_CHILD.getFragmentId(), PARENT.getRelations().get(0).treeNode());
		verify(ldesFragmentRepository, times(1)).retrieveMutableFragment(VIEW, List.of());
		verifyNoMoreInteractions(ldesFragmentRepository);
	}

}