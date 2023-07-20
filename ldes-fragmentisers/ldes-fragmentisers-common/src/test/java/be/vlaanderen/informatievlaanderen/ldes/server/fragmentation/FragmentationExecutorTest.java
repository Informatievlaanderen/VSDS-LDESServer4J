package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.ingest.MemberIngestedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.MissingRootFragmentException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.LdesFragmentIdentifier;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Fragment;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.FragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.MembersToFragmentRepository;
import io.micrometer.observation.ObservationRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class FragmentationExecutorTest {

	private static final String COLLECTION_NAME = "collectionName";
	private static final ViewName VIEW_NAME = new ViewName(COLLECTION_NAME, "view");
	private final FragmentationStrategy fragmentationStrategy = Mockito.mock(FragmentationStrategy.class);
	private final FragmentRepository fragmentRepository = mock(FragmentRepository.class);
	private final MembersToFragmentRepository membersToFragmentRepository = mock(MembersToFragmentRepository.class);
	private final FragmentationStrategyCollection fragmentationStrategyCollection = Mockito.mock(
			FragmentationStrategyCollection.class);
	private FragmentationExecutor fragmentationExecutor;

	@BeforeEach
	void setUp() {
		fragmentationExecutor = new FragmentationExecutor(
				fragmentRepository, ObservationRegistry.create(), fragmentationStrategyCollection,
				membersToFragmentRepository);
	}

	@Test
	void when_FragmentExecutionOnMemberIsCalled_RootNodeIsRetrievedAndFragmentationStrategyIsCalled() {
		when(fragmentationStrategyCollection.getFragmentationStrategyMap()).thenReturn(
				Map.of(VIEW_NAME, fragmentationStrategy));
		Fragment fragment = new Fragment(new LdesFragmentIdentifier(VIEW_NAME,
				List.of()));
		when(fragmentRepository.retrieveRootFragment(VIEW_NAME.asString()))
				.thenReturn(Optional.of(fragment));
		Member member = mock(Member.class);
		when(member.getCollectionName()).thenReturn(COLLECTION_NAME);

		fragmentationExecutor.executeFragmentation(new MemberIngestedEvent(member.getModel(), member.getLdesMemberId(),
				member.getCollectionName(), member.getSequenceNr()));

		verify(fragmentRepository, times(1))
				.retrieveRootFragment(VIEW_NAME.asString());
		verify(fragmentationStrategy, times(1)).addMemberToFragment(eq(fragment),
				any(), any(), any());
	}

	@Test
	void whenMemberIsFromDifferentCollection_thenItIsNotFragmented() {
		final ViewName alternativeViewName = new ViewName("otherCollection", "otherView");
		Fragment fragment = new Fragment(new LdesFragmentIdentifier(alternativeViewName, List.of()));
		when(fragmentRepository.retrieveRootFragment(alternativeViewName.asString()))
				.thenReturn(Optional.of(fragment));
		Member member = mock(Member.class);
		when(member.getCollectionName()).thenReturn(alternativeViewName.getCollectionName());

		fragmentationExecutor.executeFragmentation(new MemberIngestedEvent(member.getModel(), member.getLdesMemberId(),
				member.getCollectionName(), member.getSequenceNr()));

		verify(fragmentRepository, times(0))
				.retrieveRootFragment(alternativeViewName.asString());
		verify(fragmentationStrategy, times(0)).addMemberToFragment(eq(fragment),
				any(), any(), any());
	}

	@Test
	void when_RootFragmentDoesNotExist_MissingRootFragmentExceptionIsThrown() {
		when(fragmentationStrategyCollection.getFragmentationStrategyMap()).thenReturn(
				Map.of(VIEW_NAME, fragmentationStrategy));
		when(fragmentRepository
				.retrieveFragment(new LdesFragmentIdentifier(VIEW_NAME,
						List.of())))
				.thenReturn(Optional.empty());
		Member member = mock(Member.class);
		when(member.getCollectionName()).thenReturn(COLLECTION_NAME);

		MemberIngestedEvent memberIngestedEvent = new MemberIngestedEvent(member.getModel(), member.getLdesMemberId(),
				member.getCollectionName(), member.getSequenceNr());

		MissingRootFragmentException missingRootFragmentException = assertThrows(MissingRootFragmentException.class,
				() -> fragmentationExecutor.executeFragmentation(memberIngestedEvent));

		assertEquals("Could not retrieve root fragment for view collectionName/view",
				missingRootFragmentException.getMessage());
		verify(fragmentRepository, times(1))
				.retrieveRootFragment(VIEW_NAME.asString());
	}

	@Test
	void when_FragmentationExecutorIsCalledInParallel_FragmentationHappensByOneThreadAtATime() {
		when(fragmentationStrategyCollection.getFragmentationStrategyMap()).thenReturn(
				Map.of(VIEW_NAME, fragmentationStrategy));
		Fragment fragment = new Fragment(new LdesFragmentIdentifier(VIEW_NAME,
				List.of()));
		when(fragmentRepository.retrieveRootFragment(VIEW_NAME.asString()))
				.thenReturn(Optional.of(fragment));
		IntStream.range(0, 100).parallel()
				.forEach(i -> {
					Member member = mock(Member.class);
					when(member.getCollectionName()).thenReturn(COLLECTION_NAME);
					fragmentationExecutor.executeFragmentation(new MemberIngestedEvent(member.getModel(), member.getLdesMemberId(),
							member.getCollectionName(), member.getSequenceNr()));
				});

		InOrder inOrder = inOrder(fragmentRepository, fragmentationStrategy);
		inOrder.verify(fragmentRepository, times(1))
				.retrieveRootFragment(VIEW_NAME.asString());
		inOrder.verify(fragmentationStrategy,
				times(100)).addMemberToFragment(eq(fragment),
				any(), any(), any());
		inOrder.verifyNoMoreInteractions();
	}

}
