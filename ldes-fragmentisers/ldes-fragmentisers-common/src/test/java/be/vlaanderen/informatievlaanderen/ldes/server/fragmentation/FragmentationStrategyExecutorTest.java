//package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation;
//
//import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.LdesFragmentIdentifier;
//import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;
//import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Fragment;
//import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Member;
//import io.micrometer.observation.ObservationRegistry;
//import org.apache.jena.ext.com.google.common.util.concurrent.MoreExecutors;
//import org.apache.jena.rdf.model.ModelFactory;
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.Nested;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.junit.jupiter.api.extension.ExtensionContext;
//import org.junit.jupiter.params.ParameterizedTest;
//import org.junit.jupiter.params.provider.Arguments;
//import org.junit.jupiter.params.provider.ArgumentsProvider;
//import org.junit.jupiter.params.provider.ArgumentsSource;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.util.List;
//import java.util.Optional;
//import java.util.concurrent.ExecutorService;
//import java.util.function.BiConsumer;
//import java.util.stream.Stream;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class FragmentationStrategyExecutorTest {
//
// TODO TVB: 31/07/23 fixme
//	@Nested
//	class ExecuteNext {
//
//		@Mock
//		private ExecutorService executorService;
//		@Mock
//		private FragmentationStrategy fragmentationStrategy;
//		@Mock
//		private RootFragmentRetriever rootFragmentRetriever;
//
//		private final ViewName viewName = ViewName.fromString("col/viewA");
//
//		@Test
//		void when_ExecuteNextIsCalled_then_AllLogicIsWrappedByTheExecutorService() {
//			ObservationRegistry observationRegistry = mock(ObservationRegistry.class);
//			var executor = new FragmentationStrategyExecutor(viewName, fragmentationStrategy, rootFragmentRetriever,
//					observationRegistry, executorService, eventSourceService, fragmentSequenceRepository);
//
//			executor.execute();
//
//			verify(executorService).execute(any());
//			verifyNoMoreInteractions(fragmentationStrategy, rootFragmentRetriever, observationRegistry,
//					memberToFragmentRepository);
//		}
//
//		@Test
//		void when_ExecuteNextIsCalled_then_TheExecutorService_should_NotFragmentAnythingIfNotPresent() {
//			var executor = new FragmentationStrategyExecutor(viewName, fragmentationStrategy, rootFragmentRetriever,
//					ObservationRegistry.create(),
//					MoreExecutors.newDirectExecutorService(), eventSourceService, fragmentSequenceRepository);
//
//			executor.execute();
//
//			verify(memberToFragmentRepository).getNextMemberToFragment(viewName);
//			verifyNoMoreInteractions(fragmentationStrategy, rootFragmentRetriever, memberToFragmentRepository);
//		}
//
//		@Test
//		void when_ExecuteNextIsCalled_then_TheExecutorService_should_FragmentTheNextMemberIfPresent() {
//			ObservationRegistry observationRegistry = ObservationRegistry.create();
//			var executor = new FragmentationStrategyExecutor(viewName, fragmentationStrategy, rootFragmentRetriever,
//					observationRegistry, MoreExecutors.newDirectExecutorService(), eventSourceService, fragmentSequenceRepository);
//			final Member member = new Member("id", ModelFactory.createDefaultModel(), 1L);
//			when(memberToFragmentRepository.getNextMemberToFragment(viewName)).thenReturn(Optional.of(member));
//			final Fragment rootFragment = new Fragment(new LdesFragmentIdentifier(viewName, List.of()));
//			when(rootFragmentRetriever.retrieveRootFragmentOfView(eq(viewName), any())).thenReturn(rootFragment);
//
//			executor.execute();
//
//			verify(fragmentationStrategy).addMemberToFragment(eq(rootFragment), eq(member.id()), eq(member.model()),
//					any());
//			verify(memberToFragmentRepository).delete(viewName, member.sequenceNr());
//		}
//	}
//
//	@Test
//	void isPartOfCollection() {
//		final ViewName viewNameA = ViewName.fromString("col/viewA");
//		final FragmentationStrategyExecutor executorA = new FragmentationStrategyExecutor(viewNameA, null, null, null,
//				null, eventSourceService, fragmentSequenceRepository);
//
//		assertTrue(executorA.isPartOfCollection(viewNameA.getCollectionName()));
//		assertFalse(executorA.isPartOfCollection("other"));
//	}
//
//	@Test
//	void getViewName() {
//		final ViewName viewNameA = ViewName.fromString("col/viewA");
//		final FragmentationStrategyExecutor executorA = new FragmentationStrategyExecutor(viewNameA, null, null, null,
//				null, eventSourceService, fragmentSequenceRepository);
//
//		assertEquals(viewNameA, executorA.getViewName());
//	}
//
//	@ParameterizedTest
//	@ArgumentsSource(EqualityTestProvider.class)
//	void testEqualsAndHashCode(BiConsumer<Object, Object> assertion, FragmentationStrategyExecutor a,
//			FragmentationStrategyExecutor b) {
//		assertNotNull(assertion);
//		assertion.accept(a, b);
//		if (a != null && b != null) {
//			assertion.accept(a.hashCode(), b.hashCode());
//		}
//	}
//
//	static class EqualityTestProvider implements ArgumentsProvider {
//
//		private static final ViewName viewNameA = ViewName.fromString("col/viewA");
//		private static final FragmentationStrategyExecutor executorA = new FragmentationStrategyExecutor(viewNameA,
//				null, null, null, null, eventSourceService, fragmentSequenceRepository);
//
//		@Override
//		public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
//			return Stream.of(
//					Arguments.of(equals(), executorA, executorA),
//					Arguments.of(equals(), executorA,
//							new FragmentationStrategyExecutor(viewNameA, null, null, null, null, eventSourceService, fragmentSequenceRepository)),
//					Arguments.of(equals(), executorA,
//							new FragmentationStrategyExecutor(viewNameA, mock(FragmentationStrategy.class),
//									mock(RootFragmentRetriever.class), mock(ObservationRegistry.class),
//									mock(MemberToFragmentRepository.class), mock(ExecutorService.class), memberRepository, eventSourceService, fragmentSequenceRepository)),
//					Arguments.of(notEquals(), executorA,
//							new FragmentationStrategyExecutor(ViewName.fromString("col/viewB"), null, null, null,
//									null, eventSourceService, fragmentSequenceRepository)));
//		}
//
//		private static BiConsumer<Object, Object> equals() {
//			return Assertions::assertEquals;
//		}
//
//		private static BiConsumer<Object, Object> notEquals() {
//			return Assertions::assertNotEquals;
//		}
//
//	}
//
//}