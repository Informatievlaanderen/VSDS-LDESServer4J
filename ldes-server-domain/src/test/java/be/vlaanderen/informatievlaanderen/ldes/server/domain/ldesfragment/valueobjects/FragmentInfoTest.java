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

import static org.junit.jupiter.api.Assertions.*;

class FragmentInfoTest {

	public static final String VIEW = "view";
	public static final FragmentPair PARENT_FRAGMENT_PAIR = new FragmentPair("a", "b");
	public static final FragmentPair CHILD_FRAGMENT_PAIR = new FragmentPair("c", "d");

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
					Arguments.of(new FragmentInfo("otherView", List.of(PARENT_FRAGMENT_PAIR), false, null, false, 0)),
					Arguments.of(new FragmentInfo(VIEW, List.of(), false, null, false, 0)),
					Arguments.of(new FragmentInfo(VIEW, List.of(PARENT_FRAGMENT_PAIR), true, null, false, 0)),
					Arguments.of(
							new FragmentInfo(VIEW, List.of(PARENT_FRAGMENT_PAIR), false, LocalDateTime.now(), false,
									0)),
					Arguments.of(new FragmentInfo(VIEW, List.of(PARENT_FRAGMENT_PAIR), false, null, true, 0)),
					Arguments.of((Object) null),
					Arguments.of(List.of()));
		}
	}
}