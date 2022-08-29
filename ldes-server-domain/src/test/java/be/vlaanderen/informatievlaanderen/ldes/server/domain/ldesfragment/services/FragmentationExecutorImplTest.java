package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.config.LdesConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.MissingRootFragmentException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.FragmentInfo;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.entities.LdesFragmentRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class FragmentationExecutorImplTest {

	private static final String COLLECTIONAME = "mobility-hindrances";
	private final FragmentationService fragmentationService = mock(FragmentationService.class);
	private final LdesFragmentRepository ldesFragmentRepository = mock(LdesFragmentRepository.class);
	FragmentationExecutorImpl fragmentationExecutor;

	@BeforeEach
	void setUp() {
		LdesConfig ldesConfig = new LdesConfig();
		ldesConfig.setCollectionName(COLLECTIONAME);
		fragmentationExecutor = new FragmentationExecutorImpl(fragmentationService, ldesFragmentRepository, ldesConfig);
	}

	@Test
	void when_FragmentExecutionOnMemberIsCalled_RootNodeIsRetrievedAndFragmentationServiceIsCalled() {
		LdesFragment ldesFragment = new LdesFragment("id", new FragmentInfo(COLLECTIONAME, List.of()));
		when(ldesFragmentRepository.retrieveFragment(new LdesFragmentRequest(COLLECTIONAME, List.of())))
				.thenReturn(Optional.of(ldesFragment));

		fragmentationExecutor.executeFragmentation("memberId");

		verify(ldesFragmentRepository, times(1)).retrieveFragment(new LdesFragmentRequest(COLLECTIONAME, List.of()));
		verify(fragmentationService, times(1)).addMemberToFragment(ldesFragment, "memberId");
	}

	@Test
	void when_RootFragmentDoesNotExist_MissingRootFragmentExceptionIsThrown() {
		when(ldesFragmentRepository.retrieveFragment(new LdesFragmentRequest(COLLECTIONAME, List.of())))
				.thenReturn(Optional.empty());

		MissingRootFragmentException missingRootFragmentException = assertThrows(MissingRootFragmentException.class,
				() -> fragmentationExecutor.executeFragmentation("memberId"));

		assertEquals("Could not retrieve root fragment for collection mobility-hindrances",
				missingRootFragmentException.getMessage());
		verify(ldesFragmentRepository, times(1)).retrieveFragment(new LdesFragmentRequest(COLLECTIONAME, List.of()));
	}

}