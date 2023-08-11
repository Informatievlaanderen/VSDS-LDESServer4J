package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.LdesFragmentIdentifier;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Fragment;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.FragmentSequence;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.FragmentSequenceRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.EventSourceService;
import io.micrometer.observation.ObservationRegistry;
import org.apache.jena.ext.com.google.common.util.concurrent.MoreExecutors;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FragmentationStrategyExecutorTest {

	@Mock
	private ExecutorService executorService;
	@Mock
	private FragmentationStrategy fragmentationStrategy;
	@Mock
	private RootFragmentRetriever rootFragmentRetriever;
	@Mock
	private EventSourceService eventSourceService;
	@Mock
	private FragmentSequenceRepository fragmentSequenceRepository;

	@Nested
	class ExecuteNext {

		private final ViewName viewName = ViewName.fromString("col/viewA");

		@Test
		void when_ExecuteIsCalled_then_AllLogicIsWrappedByTheExecutorService() {
			ObservationRegistry observationRegistry = mock(ObservationRegistry.class);
			var executor = new FragmentationStrategyExecutor(viewName, fragmentationStrategy, rootFragmentRetriever,
					observationRegistry, executorService, eventSourceService, fragmentSequenceRepository);

			executor.execute();

			verify(executorService).execute(any());
			verifyNoMoreInteractions(fragmentationStrategy, rootFragmentRetriever, observationRegistry,
					eventSourceService, fragmentSequenceRepository);
		}

		@Test
		void when_ExecuteIsCalled_then_TheExecutorService_should_NotFragmentAnythingIfNotPresent() {
			var executor = new FragmentationStrategyExecutor(viewName, fragmentationStrategy, rootFragmentRetriever,
					ObservationRegistry.create(),
					MoreExecutors.newDirectExecutorService(), eventSourceService, fragmentSequenceRepository);
			FragmentSequence fragmentSequence = new FragmentSequence(viewName, 0L);
			when(fragmentSequenceRepository.findLastProcessedSequence(viewName))
					.thenReturn(Optional.of(fragmentSequence));

			executor.execute();

			verify(eventSourceService).findFirstByCollectionNameAndSequenceNrGreaterThan(viewName.getCollectionName(),
					fragmentSequence.sequenceNr());
			verifyNoMoreInteractions(fragmentationStrategy, rootFragmentRetriever, eventSourceService,
					fragmentSequenceRepository);
		}

		@Test
		void when_ExecuteIsCalled_then_TheExecutorService_should_FragmentTheNextMemberIfPresent() {
			ObservationRegistry observationRegistry = ObservationRegistry.create();
			var executor = new FragmentationStrategyExecutor(viewName, fragmentationStrategy, rootFragmentRetriever,
					observationRegistry, MoreExecutors.newDirectExecutorService(), eventSourceService,
					fragmentSequenceRepository);
			when(fragmentSequenceRepository.findLastProcessedSequence(viewName)).thenReturn(Optional.empty());
			String memberId = "id";
			Model memberModel = ModelFactory.createDefaultModel();
			long sequenceNr = 1L;
			when(eventSourceService.findFirstByCollectionNameAndSequenceNrGreaterThan(viewName.getCollectionName(),
					FragmentSequence.createNeverProcessedSequence(viewName).sequenceNr()))
					.thenReturn(Optional.of(createIngestMember(memberId, memberModel, sequenceNr)));
			final Fragment rootFragment = new Fragment(new LdesFragmentIdentifier(viewName, List.of()));
			when(rootFragmentRetriever.retrieveRootFragmentOfView(eq(viewName), any())).thenReturn(rootFragment);

			executor.execute();

			verify(fragmentationStrategy).addMemberToFragment(eq(rootFragment), eq(memberId), eq(memberModel), any());
			verify(fragmentSequenceRepository).saveLastProcessedSequence(new FragmentSequence(viewName, sequenceNr));
		}

		private be.vlaanderen.informatievlaanderen.ldes.server.ingest.entities.Member createIngestMember(String id,
				Model model,
				long sequenceNr) {
			return new be.vlaanderen.informatievlaanderen.ldes.server.ingest.entities.Member(id, "collectionName",
					sequenceNr, model);
		}
	}

	@Test
	void isPartOfCollection() {
		final ViewName viewNameA = ViewName.fromString("col/viewA");
		final FragmentationStrategyExecutor executorA = new FragmentationStrategyExecutor(viewNameA, null, null, null,
				null, eventSourceService, fragmentSequenceRepository);

		assertTrue(executorA.isPartOfCollection(viewNameA.getCollectionName()));
		assertFalse(executorA.isPartOfCollection("other"));
	}

	@Test
	void shutDown() throws InterruptedException {
		final ViewName viewName = ViewName.fromString("col/viewA");
		final FragmentationStrategyExecutor executor = new FragmentationStrategyExecutor(viewName, null, null, null,
				executorService, eventSourceService, fragmentSequenceRepository);

		executor.shutdown();

		verify(executorService).shutdown();
		verify(executorService).awaitTermination(anyLong(), any());
	}

	@Test
	void getViewName() {
		final ViewName viewNameA = ViewName.fromString("col/viewA");
		final FragmentationStrategyExecutor executorA = new FragmentationStrategyExecutor(viewNameA, null, null, null,
				null, eventSourceService, fragmentSequenceRepository);

		assertEquals(viewNameA, executorA.getViewName());
	}

	@ParameterizedTest
	@ArgumentsSource(EqualityTestProvider.class)
	void testEqualsAndHashCode(BiConsumer<Object, Object> assertion, FragmentationStrategyExecutor a,
			FragmentationStrategyExecutor b) {
		assertNotNull(assertion);
		assertion.accept(a, b);
		if (a != null && b != null) {
			assertion.accept(a.hashCode(), b.hashCode());
		}
	}

	static class EqualityTestProvider implements ArgumentsProvider {

		private static final ViewName viewNameA = ViewName.fromString("col/viewA");
		private static final FragmentationStrategyExecutor executorA = new FragmentationStrategyExecutor(viewNameA,
				null, null, null, null, null, null);

		@Override
		public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
			return Stream.of(
					Arguments.of(equals(), executorA, executorA),
					Arguments.of(equals(), executorA,
							new FragmentationStrategyExecutor(viewNameA, null, null, null, null, null, null)),
					Arguments.of(equals(), executorA,
							new FragmentationStrategyExecutor(viewNameA, mock(FragmentationStrategy.class),
									mock(RootFragmentRetriever.class), mock(ObservationRegistry.class),
									mock(ExecutorService.class), mock(EventSourceService.class),
									mock(FragmentSequenceRepository.class))),
					Arguments.of(notEquals(), executorA,
							new FragmentationStrategyExecutor(ViewName.fromString("col/viewB"), null, null, null,
									null, null, null)));
		}

		private static BiConsumer<Object, Object> equals() {
			return Assertions::assertEquals;
		}

		private static BiConsumer<Object, Object> notEquals() {
			return Assertions::assertNotEquals;
		}

	}

}