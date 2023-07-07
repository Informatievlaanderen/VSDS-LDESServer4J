package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.MissingRootFragmentException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.LdesFragmentIdentifier;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;
import io.micrometer.observation.ObservationRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FragmentationExecutorImplTest {

	private static final String COLLECTION_NAME = "collectionName";
	private static final ViewName VIEW_NAME = new ViewName(COLLECTION_NAME, "view");
	private final FragmentationStrategy fragmentationStrategy = mock(FragmentationStrategy.class);
	private final LdesFragmentRepository ldesFragmentRepository = mock(LdesFragmentRepository.class);
	private final FragmentationStrategyCollection fragmentationStrategyCollection = mock(
			FragmentationStrategyCollection.class);
	private FragmentationExecutorImpl fragmentationExecutor;

	@BeforeEach
	void setUp() {
		fragmentationExecutor = new FragmentationExecutorImpl(
				ldesFragmentRepository, ObservationRegistry.create(), fragmentationStrategyCollection);
	}

	@Test
	void when_FragmentExecutionOnMemberIsCalled_RootNodeIsRetrievedAndFragmentationStrategyIsCalled() {
		when(fragmentationStrategyCollection.getFragmentationStrategyMap()).thenReturn(Map.of(VIEW_NAME,fragmentationStrategy));
		LdesFragment ldesFragment = new LdesFragment(new LdesFragmentIdentifier(VIEW_NAME,
				List.of()));
		when(ldesFragmentRepository.retrieveRootFragment(VIEW_NAME.asString()))
				.thenReturn(Optional.of(ldesFragment));
		Member member = mock(Member.class);
		when(member.getCollectionName()).thenReturn(COLLECTION_NAME);

		fragmentationExecutor.executeFragmentation(member);

		verify(ldesFragmentRepository, times(1))
				.retrieveRootFragment(VIEW_NAME.asString());
		verify(fragmentationStrategy, times(1)).addMemberToFragment(eq(ldesFragment),
				eq(member), any());
	}

	@Test
	void whenMemberIsFromDifferentCollection_thenItIsNotFragmented() {
		final ViewName alternativeViewName = new ViewName("otherCollection", "otherView");
		LdesFragment ldesFragment = new LdesFragment(new LdesFragmentIdentifier(alternativeViewName, List.of()));
		when(ldesFragmentRepository.retrieveRootFragment(alternativeViewName.asString()))
				.thenReturn(Optional.of(ldesFragment));
		Member member = mock(Member.class);
		when(member.getCollectionName()).thenReturn(alternativeViewName.getCollectionName());

		fragmentationExecutor.executeFragmentation(member);

		verify(ldesFragmentRepository, times(0))
				.retrieveRootFragment(alternativeViewName.asString());
		verify(fragmentationStrategy, times(0)).addMemberToFragment(eq(ldesFragment),
				eq(member), any());
	}

	@Test
	void when_RootFragmentDoesNotExist_MissingRootFragmentExceptionIsThrown() {
		when(fragmentationStrategyCollection.getFragmentationStrategyMap()).thenReturn(Map.of(VIEW_NAME,fragmentationStrategy));
		when(ldesFragmentRepository
				.retrieveFragment(new LdesFragmentIdentifier(VIEW_NAME,
						List.of())))
				.thenReturn(Optional.empty());
		Member member = mock(Member.class);
		when(member.getCollectionName()).thenReturn(COLLECTION_NAME);

		MissingRootFragmentException missingRootFragmentException = assertThrows(MissingRootFragmentException.class,
				() -> fragmentationExecutor.executeFragmentation(member));

		assertEquals("Could not retrieve root fragment for view collectionName/view",
				missingRootFragmentException.getMessage());
		verify(ldesFragmentRepository, times(1))
				.retrieveRootFragment(VIEW_NAME.asString());
	}

	@Test
	void when_FragmentationExecutorIsCalledInParallel_FragmentationHappensByOneThreadAtATime() {
		when(fragmentationStrategyCollection.getFragmentationStrategyMap()).thenReturn(Map.of(VIEW_NAME,fragmentationStrategy));
		LdesFragment ldesFragment = new LdesFragment(new LdesFragmentIdentifier(VIEW_NAME,
				List.of()));
		when(ldesFragmentRepository.retrieveRootFragment(VIEW_NAME.asString()))
				.thenReturn(Optional.of(ldesFragment));
		IntStream.range(0, 100).parallel()
				.forEach(i -> {
					Member member = mock(Member.class);
					when(member.getCollectionName()).thenReturn(COLLECTION_NAME);
					fragmentationExecutor.executeFragmentation(member);
				});

		InOrder inOrder = inOrder(ldesFragmentRepository, fragmentationStrategy);
		inOrder.verify(ldesFragmentRepository, times(1))
				.retrieveRootFragment(VIEW_NAME.asString());
		inOrder.verify(fragmentationStrategy,
				times(100)).addMemberToFragment(eq(ldesFragment),
						any(), any());
		inOrder.verifyNoMoreInteractions();
	}

}