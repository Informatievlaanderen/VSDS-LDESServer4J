package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

import java.util.Optional;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

class RootFragmentCreatorImplTest {

	private static final String VIEW = "mobility-hindrances";
	private static final ViewName VIEW_NAME = new ViewName("collectionName", VIEW);

	private final LdesFragmentRepository ldesFragmentRepository = mock(LdesFragmentRepository.class);
	private RootFragmentCreator rootFragmentCreator;

	@BeforeEach
	void setUp() {
		rootFragmentCreator = new RootFragmentCreatorImpl(ldesFragmentRepository);
	}

	@Test
	void when_RootFragmentDoesNotExist_ItIsCreatedAndSaved() {
		when(ldesFragmentRepository.retrieveRootFragment(VIEW)).thenReturn(Optional.empty());
		rootFragmentCreator.createRootFragmentForView(VIEW_NAME);

		InOrder inOrder = inOrder(ldesFragmentRepository);
		inOrder.verify(ldesFragmentRepository, times(1)).retrieveRootFragment(VIEW);
		inOrder.verify(ldesFragmentRepository, times(1)).saveFragment(any());
		inOrder.verifyNoMoreInteractions();
	}

	@Test
	void when_RootFragmentExists_NothingHappens() {
		when(ldesFragmentRepository.retrieveRootFragment(VIEW)).thenReturn(Optional.of(mock(LdesFragment.class)));
		rootFragmentCreator.createRootFragmentForView(VIEW_NAME);

		InOrder inOrder = inOrder(ldesFragmentRepository);
		inOrder.verify(ldesFragmentRepository, times(1)).retrieveRootFragment(VIEW);
		inOrder.verifyNoMoreInteractions();
	}
}