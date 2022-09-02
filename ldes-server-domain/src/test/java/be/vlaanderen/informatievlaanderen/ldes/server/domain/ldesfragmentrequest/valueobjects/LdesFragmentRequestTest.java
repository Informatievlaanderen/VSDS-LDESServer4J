package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.valueobjects;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesmember.entities.LdesMember;
import org.junit.jupiter.api.DisplayName;
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

class LdesFragmentRequestTest {

	private static final String COLLECTION_NAME = "collectionName";
	private static final String VIEW_NAME = "view";
	public static final String KEY = "key";
	public static final String VALUE = "value";
	public static final String KEY_2 = "key2";
	public static final String VALUE_2 = "value2";

	@Test
	@DisplayName("Test Equality of LdesFragmentRequest")
	void test_EqualityOfLdesFragmentRequests() {
		LdesFragmentRequest ldesFragmentRequest = new LdesFragmentRequest(COLLECTION_NAME, VIEW_NAME,
				List.of(new FragmentPair(KEY, VALUE), new FragmentPair(KEY_2,
						VALUE_2)));
		LdesFragmentRequest otherLdesFragmentRequest = new LdesFragmentRequest(COLLECTION_NAME, VIEW_NAME,
				List.of(new FragmentPair(KEY, VALUE), new FragmentPair(KEY_2,
						VALUE_2)));
		assertEquals(ldesFragmentRequest, otherLdesFragmentRequest);
		assertEquals(ldesFragmentRequest, ldesFragmentRequest);
		assertEquals(otherLdesFragmentRequest, otherLdesFragmentRequest);
	}

	@ParameterizedTest
	@ArgumentsSource(LdesFragmentRequestArgumentsProvider.class)
	void test_InequalityOfLdesFragmentRequests(Object otherLdesFragmentRequest) {
		LdesFragmentRequest ldesFragmentRequest = new LdesFragmentRequest(COLLECTION_NAME, VIEW_NAME,
				List.of(new FragmentPair(KEY, VALUE), new FragmentPair(KEY_2,
						VALUE_2)));
		assertNotEquals(ldesFragmentRequest, otherLdesFragmentRequest);
	}

	static class LdesFragmentRequestArgumentsProvider implements
			ArgumentsProvider {

		@Override
		public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
			return Stream.of(Arguments.of(new LdesMember("some_id", null)),
					Arguments.of((Object) null),
					Arguments.of(new LdesFragmentRequest("otherCollectionName", VIEW_NAME,
							List.of(new FragmentPair(KEY, VALUE), new FragmentPair(KEY_2,
									VALUE_2)))),
					Arguments.of(new LdesFragmentRequest(COLLECTION_NAME, "otherViewName",
							List.of(new FragmentPair(KEY, VALUE), new FragmentPair(KEY_2,
									VALUE_2)))),
					Arguments.of(new LdesFragmentRequest(COLLECTION_NAME, VIEW_NAME,
							List.of(new FragmentPair(KEY_2, VALUE_2), new FragmentPair(KEY,
									VALUE)))));
		}
	}

}