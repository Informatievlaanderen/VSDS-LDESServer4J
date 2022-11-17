package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.valueobjects.FragmentPair;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.GENERATED_AT_TIME;
import static org.junit.jupiter.api.Assertions.*;

class FragmentInfoTest {

	public static final String VIEW = "view";
	public static final String TILE = "tile";
	public static final String TILE_VALUE = "tileValue";
	public static final String GENERATED_AT_TIME_VALUE = "someTime";
	public static final FragmentPair PARENT_FRAGMENT_PAIR = new FragmentPair("a", "b");
	public static final FragmentPair CHILD_FRAGMENT_PAIR = new FragmentPair("c", "d");

	@Test
	void when_ValueIsPresent_GetValueOfKeyReturnsOptionalValue() {
		FragmentInfo fragmentInfo = new FragmentInfo(
				VIEW,
				List.of(new FragmentPair(GENERATED_AT_TIME, GENERATED_AT_TIME_VALUE),
						new FragmentPair(TILE, TILE_VALUE)));
		assertValueEquals(fragmentInfo, GENERATED_AT_TIME, GENERATED_AT_TIME_VALUE);
		assertValueEquals(fragmentInfo, TILE, TILE_VALUE);
		assertEquals("/view?generatedAtTime=someTime&tile=tileValue", fragmentInfo.generateFragmentId());

	}

	@Test
	void when_ValueIsAbsent_GetValueOfKeyReturnsOptionalEmpty() {
		FragmentInfo fragmentInfo = new FragmentInfo(VIEW,
				List.of());
		Optional<String> optionalValue = fragmentInfo.getValueOfKey("unexistingKey");
		assertTrue(optionalValue.isEmpty());
	}

	@Test
	void when_childIsCreated_ViewIsSameAndFragmentPairsAreExtended() {
		FragmentInfo fragmentInfo = new FragmentInfo(VIEW,
				List.of(PARENT_FRAGMENT_PAIR));
		FragmentInfo child = fragmentInfo.createChild(CHILD_FRAGMENT_PAIR);
		assertEquals(List.of(PARENT_FRAGMENT_PAIR, CHILD_FRAGMENT_PAIR), child.getFragmentPairs());
		assertEquals(false, child.getImmutable());
		assertEquals(VIEW, child.getViewName());
	}

	@Test
	void when_fragmentInfoIsMadeImmutable_ImmutableTimeStampIsSet() {
		FragmentInfo fragmentInfo = new FragmentInfo(VIEW,
				List.of(PARENT_FRAGMENT_PAIR));
		assertFalse(fragmentInfo.getImmutable());
		assertNull(fragmentInfo.getImmutableTimestamp());
		fragmentInfo.makeImmutable();
		assertTrue(fragmentInfo.getImmutable());
		assertNotNull(fragmentInfo.getImmutableTimestamp());
	}

	private void assertValueEquals(FragmentInfo fragmentInfo, String key, String value) {
		Optional<String> optionalValue = fragmentInfo.getValueOfKey(key);
		assertTrue(optionalValue.isPresent());
		assertEquals(value, optionalValue.get());
	}

	@Test
	void test_Equality() {
		FragmentInfo fragmentInfo = new FragmentInfo(VIEW, List.of(PARENT_FRAGMENT_PAIR));
		FragmentInfo otherFragmentInfo = new FragmentInfo(VIEW, List.of(PARENT_FRAGMENT_PAIR));
		assertEquals(fragmentInfo, otherFragmentInfo);
		assertEquals(fragmentInfo.hashCode(), otherFragmentInfo.hashCode());
		assertEquals(fragmentInfo, fragmentInfo);
		assertEquals(fragmentInfo.hashCode(), fragmentInfo.hashCode());
		assertEquals(otherFragmentInfo, otherFragmentInfo);
		assertEquals(otherFragmentInfo.hashCode(), otherFragmentInfo.hashCode());
	}

	@ParameterizedTest
	@ArgumentsSource(FragmentInfoArgumentsProvider.class)
	void test_Inequality(Object otherFragmentInfo) {
		FragmentInfo fragmentInfo = new FragmentInfo(VIEW, List.of(PARENT_FRAGMENT_PAIR));
		assertNotEquals(fragmentInfo, otherFragmentInfo);
		if (otherFragmentInfo != null)
			assertNotEquals(fragmentInfo.hashCode(), otherFragmentInfo.hashCode());
	}

	static class FragmentInfoArgumentsProvider implements ArgumentsProvider {

		@Override
		public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
			return Stream.of(
					Arguments.of(new FragmentInfo("otherView", List.of(PARENT_FRAGMENT_PAIR), false, null, false)),
					Arguments.of(new FragmentInfo(VIEW, List.of(), false, null, false)),
					Arguments.of(new FragmentInfo(VIEW, List.of(PARENT_FRAGMENT_PAIR), true, null, false)),
					Arguments.of(
							new FragmentInfo(VIEW, List.of(PARENT_FRAGMENT_PAIR), false, LocalDateTime.now(), false)),
					Arguments.of(new FragmentInfo(VIEW, List.of(PARENT_FRAGMENT_PAIR), false, null, true)),
					Arguments.of((Object) null),
					Arguments.of(List.of()));
		}
	}
}