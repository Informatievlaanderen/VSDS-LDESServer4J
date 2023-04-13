package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.MissingRootFragmentException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.valueobjects.LdesFragmentRequest;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.entities.Member;
import io.micrometer.observation.ObservationRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class FragmentationExecutorImplTest {

	private static final String COLLECTION_NAME = "collectionName";
	private static final String VIEW_NAME = COLLECTION_NAME + "/view";
	private final FragmentationStrategy fragmentationStrategy = mock(FragmentationStrategy.class);
	private final LdesFragmentRepository ldesFragmentRepository = mock(LdesFragmentRepository.class);
	private FragmentationExecutorImpl fragmentationExecutor;

	@BeforeEach
	void setUp() {
		fragmentationExecutor = new FragmentationExecutorImpl(Map.of(VIEW_NAME,
				fragmentationStrategy),
				ldesFragmentRepository, ObservationRegistry.create());
	}

	@Test
	void when_FragmentExecutionOnMemberIsCalled_RootNodeIsRetrievedAndFragmentationStrategyIsCalled() {
		LdesFragment ldesFragment = new LdesFragment(COLLECTION_NAME, VIEW_NAME,
				List.of());
		when(ldesFragmentRepository.retrieveRootFragment(VIEW_NAME))
				.thenReturn(Optional.of(ldesFragment));
		Member member = mock(Member.class);
		when(member.getCollectionName()).thenReturn(COLLECTION_NAME);

		fragmentationExecutor.executeFragmentation(member);

		verify(ldesFragmentRepository, times(1))
				.retrieveRootFragment(VIEW_NAME);
		verify(fragmentationStrategy, times(1)).addMemberToFragment(eq(ldesFragment),
				eq(member), any());
	}

	@Test
	void whenMemberIsFromDifferentCollection_thenItIsNotFragmented() {
		final String alternativeViewName = "other/view";
		final String alternativeCollection = "other";
		LdesFragment ldesFragment = new LdesFragment(alternativeCollection, alternativeViewName, List.of());
		when(ldesFragmentRepository.retrieveRootFragment(alternativeViewName))
				.thenReturn(Optional.of(ldesFragment));
		Member member = mock(Member.class);
		when(member.getCollectionName()).thenReturn(alternativeCollection);

		fragmentationExecutor.executeFragmentation(member);

		verify(ldesFragmentRepository, times(0))
				.retrieveRootFragment(alternativeViewName);
		verify(fragmentationStrategy, times(0)).addMemberToFragment(eq(ldesFragment),
				eq(member), any());
	}

	@Test
	void when_RootFragmentDoesNotExist_MissingRootFragmentExceptionIsThrown() {
		when(ldesFragmentRepository
				.retrieveFragment(new LdesFragmentRequest(COLLECTION_NAME, VIEW_NAME,
						List.of()).generateFragmentId()))
				.thenReturn(Optional.empty());
		Member member = mock(Member.class);
		when(member.getCollectionName()).thenReturn(COLLECTION_NAME);

		MissingRootFragmentException missingRootFragmentException = assertThrows(MissingRootFragmentException.class,
				() -> fragmentationExecutor.executeFragmentation(member));

		assertEquals("Could not retrieve root fragment for view collectionName/view",
				missingRootFragmentException.getMessage());
		verify(ldesFragmentRepository, times(1))
				.retrieveRootFragment(VIEW_NAME);
	}

	@Test
	void when_FragmentationExecutorIsCalledInParallel_FragmentationHappensByOneThreadAtATime() {
		LdesFragment ldesFragment = new LdesFragment(COLLECTION_NAME, VIEW_NAME,
				List.of());
		when(ldesFragmentRepository.retrieveRootFragment(VIEW_NAME))
				.thenReturn(Optional.of(ldesFragment));
		IntStream.range(0, 100).parallel()
				.forEach(i -> {
					Member member = mock(Member.class);
					when(member.getCollectionName()).thenReturn(COLLECTION_NAME);
					fragmentationExecutor.executeFragmentation(member);
				});

		InOrder inOrder = inOrder(ldesFragmentRepository, fragmentationStrategy);
		inOrder.verify(ldesFragmentRepository, times(1))
				.retrieveRootFragment(VIEW_NAME);
		inOrder.verify(fragmentationStrategy,
				times(100)).addMemberToFragment(eq(ldesFragment),
						any(), any());
		inOrder.verifyNoMoreInteractions();

	}

}