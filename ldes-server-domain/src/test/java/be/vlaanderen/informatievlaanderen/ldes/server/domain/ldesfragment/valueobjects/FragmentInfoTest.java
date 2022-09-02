package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.valueobjects.FragmentPair;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.GENERATED_AT_TIME;
import static org.junit.jupiter.api.Assertions.*;

class FragmentInfoTest {

	public static final String COLLECTION_NAME = "collectionName";
	public static final String VIEW = "view";
	public static final String TILE = "tile";
	public static final String TILE_VALUE = "tileValue";
	public static final String GENERATED_AT_TIME_VALUE = "someTime";

	@Test
	void when_ValueIsPresent_GetValueOfKeyReturnsOptionalValue() {
		FragmentInfo fragmentInfo = new FragmentInfo(COLLECTION_NAME,
				VIEW,
				List.of(new FragmentPair(GENERATED_AT_TIME, GENERATED_AT_TIME_VALUE),
						new FragmentPair(TILE, TILE_VALUE)));
		assertValueEquals(fragmentInfo, GENERATED_AT_TIME, GENERATED_AT_TIME_VALUE);
		assertValueEquals(fragmentInfo, TILE, TILE_VALUE);

	}

	@Test
	void when_ValueIsAbsent_GetValueOfKeyReturnsOptionalEmpty() {
		FragmentInfo fragmentInfo = new FragmentInfo(COLLECTION_NAME, VIEW,
				List.of());
		Optional<String> optionalValue = fragmentInfo.getValueOfKey("unexistingKey");
		assertTrue(optionalValue.isEmpty());
	}

	private void assertValueEquals(FragmentInfo fragmentInfo, String key, String value) {
		Optional<String> optionalValue = fragmentInfo.getValueOfKey(key);
		assertTrue(optionalValue.isPresent());
		assertEquals(value, optionalValue.get());
	}

}