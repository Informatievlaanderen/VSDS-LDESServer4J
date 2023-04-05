package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.FragmentInfo;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.valueobjects.FragmentPair;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LdesFragmentTest {
	private static final String VIEW_NAME = "mobility-hindrances";
	private static final String FRAGMENTATION_VALUE_1 = "2020-12-28T09:36:09.72Z";
	private static final String GENERATED_AT_TIME = "generatedAtTime";

	@Test
	@DisplayName("Test if fragment is immutable or not")
	void when_LdesFragmentIsImmutable_IsImmutableReturnsTrue() {
		LdesFragment ldesFragment = new LdesFragment(
				new FragmentInfo(VIEW_NAME,
						List.of(new FragmentPair(GENERATED_AT_TIME, FRAGMENTATION_VALUE_1))));
		assertFalse(ldesFragment.isImmutable());
		ldesFragment.makeImmutable();
		assertTrue(ldesFragment.isImmutable());
	}
}