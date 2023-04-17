package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.valueobjects;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.util.List;
import java.util.stream.Stream;

import static org.apache.commons.lang3.ObjectUtils.isEmpty;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LdesFragmentRequestTest {

	private static final String VIEW = "view";
	private static final ViewName VIEW_NAME = new ViewName("collectionName", VIEW);
	public static final String KEY = "key";
	public static final String VALUE = "value";
	public static final String KEY_2 = "key2";
	public static final String VALUE_2 = "value2";

	@Test
	@DisplayName("Test Equality of LdesFragmentRequest")
	void test_EqualityOfLdesFragmentRequests() {
		LdesFragmentRequest ldesFragmentRequest = new LdesFragmentRequest(VIEW_NAME,
				List.of(new FragmentPair(KEY, VALUE), new FragmentPair(KEY_2,
						VALUE_2)));
		LdesFragmentRequest otherLdesFragmentRequest = new LdesFragmentRequest(VIEW_NAME,
				List.of(new FragmentPair(KEY, VALUE), new FragmentPair(KEY_2,
						VALUE_2)));
		assertEquals(ldesFragmentRequest, otherLdesFragmentRequest);
		assertEquals(ldesFragmentRequest, ldesFragmentRequest);
		assertEquals(otherLdesFragmentRequest, otherLdesFragmentRequest);
	}

	@ParameterizedTest
	@ArgumentsSource(LdesFragmentRequestArgumentsProvider.class)
	void test_InequalityOfLdesFragmentRequests(Object otherLdesFragmentRequest) {
		LdesFragmentRequest ldesFragmentRequest = new LdesFragmentRequest(VIEW_NAME,
				List.of(new FragmentPair(KEY, VALUE), new FragmentPair(KEY_2,
						VALUE_2)));
		assertNotEquals(ldesFragmentRequest, otherLdesFragmentRequest);
	}

	@Test
	void when_ViewRequestIsCreated_RequestHasViewNameAndEmptyList() {
		final ViewName viewName = new ViewName("collection_name", "view_name");
		LdesFragmentRequest request = LdesFragmentRequest.createViewRequest(viewName);
		assertEquals(viewName, request.viewName());
		assertTrue(isEmpty(request.fragmentPairs()));
	}

	static class LdesFragmentRequestArgumentsProvider implements
			ArgumentsProvider {

		@Override
		public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
			return Stream.of(Arguments.of(new Member("some_id", "collectionName", 0L, null, null, null, List.of())),
					Arguments.of((Object) null),
					Arguments.of(new LdesFragmentRequest(new ViewName("otherCollection", "otherView"),
							List.of(new FragmentPair(KEY, VALUE), new FragmentPair(KEY_2,
									VALUE_2)))),
					Arguments.of(new LdesFragmentRequest(VIEW_NAME,
							List.of(new FragmentPair(KEY_2, VALUE_2), new FragmentPair(KEY,
									VALUE)))));
		}
	}

}