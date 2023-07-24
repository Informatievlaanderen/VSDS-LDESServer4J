package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.MembersToFragmentRepository;
import io.micrometer.observation.ObservationRegistry;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.util.function.BiConsumer;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class FragmentationStrategyExecutorTest {

	@Test
	void execute() {

	}

	@Test
	void isPartOfCollection() {
		final ViewName viewNameA = ViewName.fromString("col/viewA");
		final FragmentationStrategyExecutor executorA = new FragmentationStrategyExecutor(viewNameA, null, null, null,
				null);

		assertTrue(executorA.isPartOfCollection(viewNameA.getCollectionName()));
		assertFalse(executorA.isPartOfCollection("other"));
	}

	@Test
	void getViewName() {
		final ViewName viewNameA = ViewName.fromString("col/viewA");
		final FragmentationStrategyExecutor executorA = new FragmentationStrategyExecutor(viewNameA, null, null, null,
				null);

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
				null, null, null, null);

		@Override
		public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
			return Stream.of(
					Arguments.of(equals(), executorA, executorA),
					Arguments.of(equals(), executorA,
							new FragmentationStrategyExecutor(viewNameA, null, null, null, null)),
					Arguments.of(equals(), executorA,
							new FragmentationStrategyExecutor(viewNameA, mock(FragmentationStrategy.class),
									mock(RootFragmentRetriever.class), mock(ObservationRegistry.class),
									mock(MembersToFragmentRepository.class))),
					Arguments.of(notEquals(), executorA,
							new FragmentationStrategyExecutor(ViewName.fromString("col/viewB"), null, null, null,
									null)));
		}

		private static BiConsumer<Object, Object> equals() {
			return Assertions::assertEquals;
		}

		private static BiConsumer<Object, Object> notEquals() {
			return Assertions::assertNotEquals;
		}

	}

}