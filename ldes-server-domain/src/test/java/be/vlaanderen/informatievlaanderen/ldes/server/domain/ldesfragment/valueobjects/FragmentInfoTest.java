package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.entities.FragmentPair;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class FragmentInfoTest {

	@Test
	void when_ValueIsPresent_GetValueOfKeyReturnsOptionalValue() {
		FragmentInfo fragmentInfo = new FragmentInfo("collectionName",
				List.of(new FragmentPair("generatedAtTime", "someTime"), new FragmentPair("tile", "someTile")));
		assertValueEquals(fragmentInfo, "generatedAtTime", "someTime");
		assertValueEquals(fragmentInfo, "tile", "someTile");

	}

	@Test
	void when_ValueIsAbsent_GetValueOfKeyReturnsOptionalEmpty() {
		FragmentInfo fragmentInfo = new FragmentInfo("collectionName", List.of());
		Optional<String> optionalValue = fragmentInfo.getValueOfKey("unexistingKey");
		assertTrue(optionalValue.isEmpty());
	}

	private void assertValueEquals(FragmentInfo fragmentInfo, String key, String value) {
		Optional<String> optionalValue = fragmentInfo.getValueOfKey(key);
		assertTrue(optionalValue.isPresent());
		assertEquals(value, optionalValue.get());
	}

}