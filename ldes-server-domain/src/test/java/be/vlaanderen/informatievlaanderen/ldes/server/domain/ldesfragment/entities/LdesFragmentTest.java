package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.FragmentInfo;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.valueobjects.FragmentPair;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class LdesFragmentTest {
	private static final String VIEW_NAME = "mobility-hindrances";
	private static final String FRAGMENTATION_VALUE_1 = "2020-12-28T09:36:09.72Z";
	private static final String GENERATED_AT_TIME = "generatedAtTime";

	private static final String FRAGMENTATION_VALUE_2 = "0/0/0";
	private static final String TILE = "tile";

	@Test
	void when_LdesFragmentIsImmutable_IsImmutableReturnsTrue() {
		LdesFragment ldesFragment = new LdesFragment(
				new FragmentInfo(VIEW_NAME,
						List.of(new FragmentPair(GENERATED_AT_TIME, FRAGMENTATION_VALUE_1))));
		assertFalse(ldesFragment.isImmutable());
		ldesFragment.makeImmutable();
		assertTrue(ldesFragment.isImmutable());
	}

	@Test
	void get_FragmentId() {
		LdesFragment ldesFragment = new LdesFragment(
				new FragmentInfo(VIEW_NAME,
						List.of(new FragmentPair(GENERATED_AT_TIME, FRAGMENTATION_VALUE_1),
								new FragmentPair(TILE, FRAGMENTATION_VALUE_2))));

		assertEquals("/mobility-hindrances?generatedAtTime=2020-12-28T09:36:09.72Z&tile=0/0/0",
				ldesFragment.getFragmentId());
		assertEquals("/mobility-hindrances?generatedAtTime=2020-12-28T09:36:09.72Z", ldesFragment.getParentId());

		ldesFragment = new LdesFragment(
				new FragmentInfo(VIEW_NAME, List.of()));
		assertEquals("/mobility-hindrances",
				ldesFragment.getFragmentId());
		assertEquals("root", ldesFragment.getParentId());

	}

	@Test
	void when_ValueIsAbsent_GetValueOfKeyReturnsOptionalEmpty() {
		LdesFragment ldesFragment = new LdesFragment(new FragmentInfo(VIEW_NAME,
				List.of(new FragmentPair(GENERATED_AT_TIME, FRAGMENTATION_VALUE_1),
						new FragmentPair(TILE, FRAGMENTATION_VALUE_2))));
		assertTrue(ldesFragment.getValueOfKey("unexistingKey").isEmpty());
		assertEquals(Optional.of(FRAGMENTATION_VALUE_1), ldesFragment.getValueOfKey(GENERATED_AT_TIME));
		assertEquals(Optional.of(FRAGMENTATION_VALUE_2), ldesFragment.getValueOfKey(TILE));
	}
}