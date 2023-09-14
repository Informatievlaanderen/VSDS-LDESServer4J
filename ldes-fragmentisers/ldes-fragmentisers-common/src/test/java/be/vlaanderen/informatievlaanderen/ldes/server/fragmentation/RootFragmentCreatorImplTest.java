package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Fragment;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.factory.RootFragmentCreator;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.factory.RootFragmentCreatorImpl;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.FragmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

import java.util.Optional;

import static org.mockito.Mockito.*;

class RootFragmentCreatorImplTest {

	private static final ViewName VIEW_NAME = new ViewName("collectionName", "mobility-hindrances");

	private final FragmentRepository fragmentRepository = mock(FragmentRepository.class);
	private RootFragmentCreator rootFragmentCreator;

	@BeforeEach
	void setUp() {
		rootFragmentCreator = new RootFragmentCreatorImpl(fragmentRepository);
	}

	@Test
	void when_RootFragmentDoesNotExist_ItIsCreatedAndSaved() {
		rootFragmentCreator.createRootFragmentForView(VIEW_NAME);

		InOrder inOrder = inOrder(fragmentRepository);
		inOrder.verify(fragmentRepository, times(1)).retrieveRootFragment(VIEW_NAME.asString());
		inOrder.verify(fragmentRepository, times(1)).saveFragment(any());
		inOrder.verifyNoMoreInteractions();
	}

	@Test
	void when_RootFragmentExists_NothingHappens() {
		when(fragmentRepository.retrieveRootFragment(VIEW_NAME.asString()))
				.thenReturn(Optional.of(mock(Fragment.class)));
		rootFragmentCreator.createRootFragmentForView(VIEW_NAME);

		InOrder inOrder = inOrder(fragmentRepository);
		inOrder.verify(fragmentRepository, times(1)).retrieveRootFragment(VIEW_NAME.asString());
		inOrder.verifyNoMoreInteractions();
	}
}