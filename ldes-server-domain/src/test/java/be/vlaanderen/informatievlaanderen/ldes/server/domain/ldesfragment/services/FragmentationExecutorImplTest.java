package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.MissingRootFragmentException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRepository;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class FragmentationExecutorImplTest {

	private static final String COLLECTION_NAME = "collectionName";
	private static final ViewName VIEW_NAME = new ViewName(COLLECTION_NAME, "view");
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
		LdesFragment ldesFragment = new LdesFragment(VIEW_NAME,
				List.of());
		when(ldesFragmentRepository.retrieveRootFragment(VIEW_NAME.getFullName()))
				.thenReturn(Optional.of(ldesFragment));
		Member member = mock(Member.class);
		when(member.getCollectionName()).thenReturn(COLLECTION_NAME);

		fragmentationExecutor.executeFragmentation(member);

		verify(ldesFragmentRepository, times(1))
				.retrieveRootFragment(VIEW_NAME.getFullName());
		verify(fragmentationStrategy, times(1)).addMemberToFragment(eq(ldesFragment),
				eq(member), any());
	}

	@Test
	void whenMemberIsFromDifferentCollection_thenItIsNotFragmented() {
		final ViewName alternativeViewName = new ViewName("otherCollection", "otherView");
		LdesFragment ldesFragment = new LdesFragment(alternativeViewName, List.of());
		when(ldesFragmentRepository.retrieveRootFragment(alternativeViewName.getFullName()))
				.thenReturn(Optional.of(ldesFragment));
		Member member = mock(Member.class);
		when(member.getCollectionName()).thenReturn(alternativeViewName.getCollectionName());

		fragmentationExecutor.executeFragmentation(member);

		verify(ldesFragmentRepository, times(0))
				.retrieveRootFragment(alternativeViewName.getFullName());
		verify(fragmentationStrategy, times(0)).addMemberToFragment(eq(ldesFragment),
				eq(member), any());
	}

	@Test
	void when_RootFragmentDoesNotExist_MissingRootFragmentExceptionIsThrown() {
		when(ldesFragmentRepository
				.retrieveFragment(new LdesFragment(VIEW_NAME,
						List.of()).getFragmentId()))
				.thenReturn(Optional.empty());
		Member member = mock(Member.class);
		when(member.getCollectionName()).thenReturn(COLLECTION_NAME);

		MissingRootFragmentException missingRootFragmentException = assertThrows(MissingRootFragmentException.class,
				() -> fragmentationExecutor.executeFragmentation(member));

		assertEquals("Could not retrieve root fragment for view collectionName/view",
				missingRootFragmentException.getMessage());
		verify(ldesFragmentRepository, times(1))
				.retrieveRootFragment(VIEW_NAME.getFullName());
	}

	@Test
	void when_FragmentationExecutorIsCalledInParallel_FragmentationHappensByOneThreadAtATime() {
		LdesFragment ldesFragment = new LdesFragment(VIEW_NAME,
				List.of());
		when(ldesFragmentRepository.retrieveRootFragment(VIEW_NAME.getFullName()))
				.thenReturn(Optional.of(ldesFragment));
		IntStream.range(0, 100).parallel()
				.forEach(i -> {
					Member member = mock(Member.class);
					when(member.getCollectionName()).thenReturn(COLLECTION_NAME);
					fragmentationExecutor.executeFragmentation(member);
				});

		InOrder inOrder = inOrder(ldesFragmentRepository, fragmentationStrategy);
		inOrder.verify(ldesFragmentRepository, times(1))
				.retrieveRootFragment(VIEW_NAME.getFullName());
		inOrder.verify(fragmentationStrategy,
				times(100)).addMemberToFragment(eq(ldesFragment),
						any(), any());
		inOrder.verifyNoMoreInteractions();

	}

}