package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.FragmentationMember;
import io.micrometer.observation.ObservationRegistry;
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

import java.util.concurrent.ExecutorService;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FragmentationStrategyBatchExecutorTest {

	@Mock
	private ExecutorService executorService;
	@Mock
	private FragmentationStrategy fragmentationStrategy;
	@Mock
	private RootBucketRetriever rootBucketRetriever;

	@Nested
	class ExecuteNext {

		private final ViewName viewName = ViewName.fromString("col/viewA");

		@Test
		void when_ExecuteIsCalled_then_AllLogicIsWrappedByTheExecutorService() {
			ObservationRegistry observationRegistry = ObservationRegistry.NOOP;
			var executor = new FragmentationStrategyBatchExecutor(viewName, fragmentationStrategy,
					observationRegistry);

			var member = mock(FragmentationMember.class);

			executor.bucketise(member);

			verify(rootBucketRetriever).retrieveRootBucket(any());
			verify(fragmentationStrategy).addMemberToBucket(any(), eq(member), any());
			verifyNoMoreInteractions(fragmentationStrategy, rootBucketRetriever);
		}
	}

	@Test
	void isPartOfCollection() {
		final ViewName viewNameA = ViewName.fromString("col/viewA");
		var executorA = new FragmentationStrategyBatchExecutor(viewNameA, null,
				null);

		assertTrue(executorA.isPartOfCollection(viewNameA.getCollectionName()));
		assertFalse(executorA.isPartOfCollection("other"));
	}

	@Test
	void getViewName() {
		final ViewName viewNameA = ViewName.fromString("col/viewA");
		var executorA = new FragmentationStrategyBatchExecutor(viewNameA, null,
				null);

		assertEquals(viewNameA, executorA.getViewName());
	}

	@ParameterizedTest
	@ArgumentsSource(EqualityTestProvider.class)
	void testEqualsAndHashCode(BiConsumer<Object, Object> assertion, FragmentationStrategyBatchExecutor a,
	                           FragmentationStrategyBatchExecutor b) {
		assertNotNull(assertion);
		assertion.accept(a, b);
		if (a != null && b != null) {
			assertion.accept(a.hashCode(), b.hashCode());
		}
	}

	static class EqualityTestProvider implements ArgumentsProvider {

		private static final ViewName viewNameA = ViewName.fromString("col/viewA");
		private static final FragmentationStrategyBatchExecutor executorA = new FragmentationStrategyBatchExecutor(viewNameA,
				null, null);

		@Override
		public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
			return Stream.of(
					Arguments.of(equals(), executorA, executorA),
					Arguments.of(equals(), executorA,
							new FragmentationStrategyBatchExecutor(viewNameA, null, null)),
					Arguments.of(equals(), executorA,
							new FragmentationStrategyBatchExecutor(viewNameA, mock(FragmentationStrategy.class),
									mock(ObservationRegistry.class))),
					Arguments.of(notEquals(), executorA,
							new FragmentationStrategyBatchExecutor(ViewName.fromString("col/viewB"), null, null)));
		}

		private static BiConsumer<Object, Object> equals() {
			return Assertions::assertEquals;
		}

		private static BiConsumer<Object, Object> notEquals() {
			return Assertions::assertNotEquals;
		}

	}

}
