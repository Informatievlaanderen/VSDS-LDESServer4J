package be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects;

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

class ViewNameTest {

	@Test
	void withCollectionName() {
		ViewName base = new ViewName("colA", "viewA");
		ViewName cloneWithOtherCollectionName = base.withCollectionName("colB");
		assertEquals("colB", cloneWithOtherCollectionName.getCollectionName());
		assertEquals("colB/viewA", cloneWithOtherCollectionName.asString());
	}

	@Test
	void test_asString() {
		ViewName base = new ViewName("colA", "viewA");
		assertEquals("colA/viewA", base.asString());
	}

	@Test
	void getCollectionName() {
		ViewName base = new ViewName("colA", "viewA");
		assertEquals("colA", base.getCollectionName());
	}

	@Test
	void getCollectionIri() {
		ViewName base = new ViewName("colA", "viewA");

		assertEquals("http://localhost.dev/colA", base.getCollectionIri("http://localhost.dev"));
	}

	@Test
	void getViewNameIri() {
		ViewName base = new ViewName("colA", "viewA");

		assertEquals("http://localhost.dev/colA/viewA", base.getViewNameIri("http://localhost.dev"));
	}

	@ParameterizedTest
	@ArgumentsSource(EqualityTestProvider.class)
	void testEqualsAndHashCode(BiConsumer<Object, Object> assertion, ViewName a, ViewName b) {
		assertNotNull(assertion);
		assertion.accept(a, b);
		if (a != null && b != null) {
			assertion.accept(a.hashCode(), b.hashCode());
		}
	}

	static class EqualityTestProvider implements ArgumentsProvider {

		private static final String collectionA = "collectionA";
		private static final String nameA = "nameA";
		private static final ViewName viewNameA = new ViewName(collectionA, nameA);

		@Override
		public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
			return Stream.of(
					Arguments.of(equals(), viewNameA, viewNameA),
					Arguments.of(equals(), new ViewName(collectionA, nameA), viewNameA),
					Arguments.of(notEquals(), new ViewName("otherCollection", nameA), viewNameA),
					Arguments.of(notEquals(), new ViewName(collectionA, "otherName"), viewNameA),
					Arguments.of(notEquals(), new ViewName("otherCollection", "otherName"), viewNameA),
					Arguments.of(notEquals(), null, viewNameA),
					Arguments.of(notEquals(), viewNameA, null));
		}

		private static BiConsumer<Object, Object> equals() {
			return Assertions::assertEquals;
		}

		private static BiConsumer<Object, Object> notEquals() {
			return Assertions::assertNotEquals;
		}

	}

	@Test
	void fromString_shouldCreateInstanceWithNames_whenInputContainsForwardSlashDelimiter() {
		ViewName viewName = ViewName.fromString("collection/view");
		assertEquals("collection/view", viewName.asString());
		assertEquals("collection", viewName.getCollectionName());
	}

	@Test
	void fromString_shouldThrowException_whenForwardSlashIsMissing() {
		var exception = assertThrows(IllegalArgumentException.class, () -> ViewName.fromString("myView"));
		assertEquals("Invalid full view name: myView. '/' char expected after collectionName.",
				exception.getMessage());
	}

	@Test
	void fromString_shouldThrowException_whenMoreThanOneForwardSlash() {
		var exception = assertThrows(IllegalArgumentException.class, () -> ViewName.fromString("/myCollection/myView"));
		assertEquals("Invalid full view name: /myCollection/myView. Exactly one '/' char expected.",
				exception.getMessage());
	}
}