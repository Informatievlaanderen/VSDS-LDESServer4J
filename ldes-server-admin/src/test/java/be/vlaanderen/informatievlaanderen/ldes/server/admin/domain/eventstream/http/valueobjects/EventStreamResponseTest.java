package be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.eventstream.http.valueobjects;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.dcat.dcatdataset.entities.DcatDataset;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.EventStream;
import org.apache.jena.rdf.model.ModelFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.util.List;
import java.util.stream.Stream;

import static org.apache.jena.rdf.model.ResourceFactory.createProperty;
import static org.apache.jena.rdf.model.ResourceFactory.createResource;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class EventStreamResponseTest {
	private static final String COLLECTION = "collection";
	private static final String TIMESTAMP_PATH = "generatedAt";
	private static final String VERSION_OF_PATH = "isVersionOf";
	private static final String MEMBER_TYPE_PATH = "memberType";
	private static final boolean HAS_DEFAULT_VIEW = false;
	private static final EventStreamResponse EVENT_STREAM_RESPONSE = new EventStreamResponse(COLLECTION, TIMESTAMP_PATH,
			VERSION_OF_PATH, MEMBER_TYPE_PATH, List.of(), ModelFactory.createDefaultModel());
	private static final EventStreamResponse EVENT_STREAM_RESPONSE_WITH_DATASET = new EventStreamResponse(COLLECTION,
			TIMESTAMP_PATH,
			VERSION_OF_PATH, MEMBER_TYPE_PATH, List.of(), ModelFactory.createDefaultModel(),
			new DcatDataset(COLLECTION, ModelFactory.createDefaultModel().add(createResource("subject"),
					createProperty("predicate"), "value")));

	@Test
	void test_equality() {
		EventStreamResponse other = new EventStreamResponse(COLLECTION, TIMESTAMP_PATH, VERSION_OF_PATH,
				MEMBER_TYPE_PATH, List.of(),
				ModelFactory.createDefaultModel());

		assertEquals(EVENT_STREAM_RESPONSE, other);
		assertEquals(EVENT_STREAM_RESPONSE, EVENT_STREAM_RESPONSE);
	}

	@Test
	void test_equality_with_dataset() {
		EventStreamResponse other = new EventStreamResponse(COLLECTION, TIMESTAMP_PATH, VERSION_OF_PATH,
				MEMBER_TYPE_PATH, List.of(),
				ModelFactory.createDefaultModel(),
				new DcatDataset(COLLECTION, ModelFactory.createDefaultModel().add(createResource("subject"),
						createProperty("predicate"), "value")));

		assertEquals(EVENT_STREAM_RESPONSE_WITH_DATASET, other);
		assertEquals(EVENT_STREAM_RESPONSE_WITH_DATASET, EVENT_STREAM_RESPONSE_WITH_DATASET);
	}

	@ParameterizedTest(name = "{0} is not equal")
	@ArgumentsSource(EventStreamResponseArgumentsProvider.class)
	void test_inEquality(Object other) {
		assertNotEquals(EVENT_STREAM_RESPONSE, other);

		if (other != null) {
			assertNotEquals(EVENT_STREAM_RESPONSE.hashCode(), other.hashCode());
		}
	}

	static class EventStreamResponseArgumentsProvider implements ArgumentsProvider {

		@Override
		public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) throws Exception {
			return Stream.of(
					Arguments.of("Shacl",
							new EventStreamResponse(COLLECTION, TIMESTAMP_PATH, VERSION_OF_PATH, MEMBER_TYPE_PATH,
									List.of(),
									ModelFactory.createDefaultModel().add(createResource(),
											RdfConstants.IS_PART_OF_PROPERTY, "object"))),
					Arguments.of("VersionPath",
							new EventStreamResponse(COLLECTION, TIMESTAMP_PATH, "other", MEMBER_TYPE_PATH,
									List.of(),
									ModelFactory.createDefaultModel())),
					Arguments.of("timestampPath",
							new EventStreamResponse(COLLECTION, "other", VERSION_OF_PATH, MEMBER_TYPE_PATH,
									List.of(),
									ModelFactory.createDefaultModel())),
					Arguments.of("collection",
							new EventStreamResponse("other", TIMESTAMP_PATH, VERSION_OF_PATH, MEMBER_TYPE_PATH,
									List.of(),
									ModelFactory.createDefaultModel())),
					Arguments.of("memberType",
							new EventStreamResponse(COLLECTION, TIMESTAMP_PATH, VERSION_OF_PATH, "other",
									List.of(),
									ModelFactory.createDefaultModel())),
					Arguments.of("HasDefaultView",
							new EventStreamResponse(COLLECTION, TIMESTAMP_PATH, VERSION_OF_PATH, MEMBER_TYPE_PATH,
									List.of(),
									ModelFactory.createDefaultModel())),
					Arguments.of("null", null),
					Arguments.of("dataset", EVENT_STREAM_RESPONSE_WITH_DATASET),
					Arguments.of(new EventStream(COLLECTION, TIMESTAMP_PATH, VERSION_OF_PATH, MEMBER_TYPE_PATH)));
		}
	}
}
