package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.MissingRootFragmentException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.FragmentInfo;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.valueobjects.LdesFragmentRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.IntStream;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.TracerMockHelper.mockTracer;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class FragmentationExecutorImplTest {

	private static final String VIEW_NAME = "view";
	private final FragmentationService fragmentationService = mock(FragmentationService.class);
	private final LdesFragmentRepository ldesFragmentRepository = mock(LdesFragmentRepository.class);
	private FragmentationExecutorImpl fragmentationExecutor;

	@BeforeEach
	void setUp() {
		fragmentationExecutor = new FragmentationExecutorImpl(Map.of(VIEW_NAME, fragmentationService),
				ldesFragmentRepository, mockTracer());
	}

	@Test
	void when_FragmentExecutionOnMemberIsCalled_RootNodeIsRetrievedAndFragmentationServiceIsCalled() {
		LdesFragment ldesFragment = new LdesFragment("id", new FragmentInfo(VIEW_NAME, List.of()));
		when(ldesFragmentRepository.retrieveRootFragment(VIEW_NAME))
				.thenReturn(Optional.of(ldesFragment));

		fragmentationExecutor.executeFragmentation("memberId");

		verify(ldesFragmentRepository, times(1))
				.retrieveRootFragment(VIEW_NAME);
		verify(fragmentationService, times(1)).addMemberToFragment(eq(ldesFragment),
				eq("memberId"), any());
	}

	@Test
	void when_RootFragmentDoesNotExist_MissingRootFragmentExceptionIsThrown() {
		when(ldesFragmentRepository.retrieveFragment(new LdesFragmentRequest(VIEW_NAME, List.of())))
				.thenReturn(Optional.empty());

		MissingRootFragmentException missingRootFragmentException = assertThrows(MissingRootFragmentException.class,
				() -> fragmentationExecutor.executeFragmentation("memberId"));

		assertEquals("Could not retrieve root fragment for view view",
				missingRootFragmentException.getMessage());
		verify(ldesFragmentRepository, times(1))
				.retrieveRootFragment(VIEW_NAME);
	}

	@Test
	void when_FragmentationExecutorIsCalledInParallel_FragmentationHappensByOneThreadAtATime() {
		LdesFragment ldesFragment = new LdesFragment("id", new FragmentInfo(VIEW_NAME, List.of()));
		when(ldesFragmentRepository.retrieveRootFragment(VIEW_NAME))
				.thenReturn(Optional.of(ldesFragment));
		IntStream.range(0, 100).parallel().forEach(i -> fragmentationExecutor.executeFragmentation("memberId" + i));

		InOrder inOrder = inOrder(ldesFragmentRepository, fragmentationService);
		IntStream.range(0, 100).forEach(i -> {
			inOrder.verify(ldesFragmentRepository, times(1))
					.retrieveRootFragment(VIEW_NAME);
			inOrder.verify(fragmentationService, times(1)).addMemberToFragment(eq(ldesFragment),
					any(), any());
		});
		inOrder.verifyNoMoreInteractions();

	}

}