package be.vlaanderen.informatievlaanderen.ldes.server.rest.eventstream.valueobjects;

import be.vlaanderen.informatievlaanderen.ldes.server.fetching.entities.TreeNode;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.entities.Member;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class EventStreamTest {

	private static final String COLLECTION = "collection";
	private static final String TIMESTAMP_PATH = "timestampPath";
	private static final String VERSION_OF_PATH = "versionOfPath";
	private static final String SHAPE = "shape";

	private static final String TREE_NODE_ID = "id";

	@Test
	void test_EqualityOfEventStreams() {
		EventStream eventStream = new EventStream(COLLECTION,
				TIMESTAMP_PATH, VERSION_OF_PATH, SHAPE,
				List.of(new TreeNode(TREE_NODE_ID, false, false, List.of(), List.of(), "collectionName")));
		EventStream otherEventStream = new EventStream(COLLECTION,
				TIMESTAMP_PATH, VERSION_OF_PATH, SHAPE,
				List.of(new TreeNode(TREE_NODE_ID, false, false, List.of(), List.of(), "collectionName")));

		assertEquals(eventStream, otherEventStream);
		assertEquals(eventStream, eventStream);
		assertEquals(otherEventStream, otherEventStream);
	}

	@ParameterizedTest
	@ArgumentsSource(EventStreamArgumentProvider.class)
	void test_InequalityOfEventStreams(Object otherEventStream) {
		EventStream eventStream = new EventStream(COLLECTION, TIMESTAMP_PATH, VERSION_OF_PATH, SHAPE, List.of(
				new TreeNode(TREE_NODE_ID, false, false, List.of(), List.of(), "collectionName")));

		assertNotEquals(eventStream, otherEventStream);
	}

	static class EventStreamArgumentProvider implements ArgumentsProvider {
		@Override
		public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) {
			return Stream.of(
					Arguments.of((Object) null),
					Arguments.of(new Member("id", "collectionName", 0L, null)),
					Arguments.of(new EventStream(COLLECTION,
							"other timestamp path", "other version", SHAPE, List.of())),
					Arguments.of(new EventStream("Other collection", TIMESTAMP_PATH, VERSION_OF_PATH, SHAPE,
							List.of(new TreeNode(TREE_NODE_ID, false, false, List.of(), List.of(),
									"collectionName")))),
					Arguments.of(new EventStream("Other collection", "other timestamp path",
							"other version", "other shape", null)));
		}
	}
}