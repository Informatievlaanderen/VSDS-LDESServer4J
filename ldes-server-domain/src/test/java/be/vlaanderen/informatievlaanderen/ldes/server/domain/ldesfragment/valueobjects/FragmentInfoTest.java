package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.valueobjects.FragmentPair;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.GENERATED_AT_TIME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

	private void assertValueEquals(FragmentInfo fragmentInfo, String key, String value) {
		Optional<String> optionalValue = fragmentInfo.getValueOfKey(key);
		assertTrue(optionalValue.isPresent());
		assertEquals(value, optionalValue.get());
	}

}