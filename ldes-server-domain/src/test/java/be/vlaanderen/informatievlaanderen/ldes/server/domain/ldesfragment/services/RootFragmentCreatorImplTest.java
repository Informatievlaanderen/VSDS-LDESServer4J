package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.config.LdesConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

import java.util.Optional;

import static org.mockito.Mockito.*;

class RootFragmentCreatorImplTest {

	private final LdesFragmentRepository ldesFragmentRepository = mock(LdesFragmentRepository.class);
	private RootFragmentCreator rootFragmentCreator;

	@BeforeEach
	void setUp() {
		LdesConfig ldesConfig = new LdesConfig();
		ldesConfig.setHostName("hostname");
		rootFragmentCreator = new RootFragmentCreatorImpl(ldesFragmentRepository, ldesConfig);
	}

	@Test
	void when_RootFragmentDoesNotExist_ItIsCreatedAndSaved() {
		when(ldesFragmentRepository.retrieveRootFragment("view")).thenReturn(Optional.empty());
		rootFragmentCreator.createRootFragmentForView("view");

		InOrder inOrder = inOrder(ldesFragmentRepository);
		inOrder.verify(ldesFragmentRepository, times(1)).retrieveRootFragment("view");
		inOrder.verify(ldesFragmentRepository, times(1)).saveFragment(any());
		inOrder.verifyNoMoreInteractions();
	}

	@Test
	void when_RootFragmentExists_NothingHappens() {
		when(ldesFragmentRepository.retrieveRootFragment("view")).thenReturn(Optional.of(mock(LdesFragment.class)));
		rootFragmentCreator.createRootFragmentForView("view");

		InOrder inOrder = inOrder(ldesFragmentRepository);
		inOrder.verify(ldesFragmentRepository, times(1)).retrieveRootFragment("view");
		inOrder.verifyNoMoreInteractions();
	}
}