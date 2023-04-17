package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.valueobjects.FragmentPair;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LdesFragmentTest {
	private static final String VIEW = "mobility-hindrances";
	private static final ViewName VIEW_NAME = new ViewName("collectionName", VIEW);
	private static final String FRAGMENTATION_VALUE_1 = "2020-12-28T09:36:09.72Z";
	private static final String GENERATED_AT_TIME = "generatedAtTime";
	private static final String FRAGMENTATION_VALUE_2 = "0/0/0";
	private static final String TILE = "tile";
	public static final FragmentPair PARENT_FRAGMENT_PAIR = new FragmentPair("a", "b");
	public static final FragmentPair CHILD_FRAGMENT_PAIR = new FragmentPair("c", "d");

	@Test
	void when_LdesFragmentIsImmutable_IsImmutableReturnsTrue() {
		LdesFragment ldesFragment = new LdesFragment(
				VIEW_NAME,
				List.of(new FragmentPair(GENERATED_AT_TIME, FRAGMENTATION_VALUE_1)));
		assertFalse(ldesFragment.isImmutable());
		ldesFragment.makeImmutable();
		assertTrue(ldesFragment.isImmutable());
	}

	@Test
	void get_FragmentId() {
		LdesFragment ldesFragment = new LdesFragment(
				VIEW_NAME,
				List.of(new FragmentPair(GENERATED_AT_TIME, FRAGMENTATION_VALUE_1),
						new FragmentPair(TILE, FRAGMENTATION_VALUE_2)));

		assertEquals("/mobility-hindrances?generatedAtTime=2020-12-28T09:36:09.72Z&tile=0/0/0",
				ldesFragment.getFragmentId());
		assertEquals("/mobility-hindrances?generatedAtTime=2020-12-28T09:36:09.72Z", ldesFragment.getParentId());

		ldesFragment = new LdesFragment(
				VIEW_NAME, List.of());
		assertEquals("/mobility-hindrances",
				ldesFragment.getFragmentId());
		assertEquals("root", ldesFragment.getParentId());

	}

	@Test
	void when_ValueIsAbsent_GetValueOfKeyReturnsOptionalEmpty() {
		LdesFragment ldesFragment = new LdesFragment(VIEW_NAME,
				List.of(new FragmentPair(GENERATED_AT_TIME, FRAGMENTATION_VALUE_1),
						new FragmentPair(TILE, FRAGMENTATION_VALUE_2)));
		assertTrue(ldesFragment.getValueOfKey("unexistingKey").isEmpty());
		assertEquals(Optional.of(FRAGMENTATION_VALUE_1), ldesFragment.getValueOfKey(GENERATED_AT_TIME));
		assertEquals(Optional.of(FRAGMENTATION_VALUE_2), ldesFragment.getValueOfKey(TILE));
	}

	@Test
	void when_childIsCreated_ViewIsSameAndFragmentPairsAreExtended() {
		ViewName viewName = VIEW_NAME;
		LdesFragment ldesFragment = new LdesFragment(viewName, List.of(PARENT_FRAGMENT_PAIR));
		LdesFragment child = ldesFragment.createChild(CHILD_FRAGMENT_PAIR);
		assertEquals(List.of(PARENT_FRAGMENT_PAIR, CHILD_FRAGMENT_PAIR), child.getFragmentPairs());
		assertFalse(child.isImmutable());
		assertEquals(viewName, child.getViewName());
	}

	@Test
	void when_LdesFragmentIsMadeImmutable_ImmutableTimeStampIsSet() {
		LdesFragment ldesFragment = new LdesFragment(VIEW_NAME,
				List.of(PARENT_FRAGMENT_PAIR));
		assertFalse(ldesFragment.isImmutable());
		assertNull(ldesFragment.getImmutableTimestamp());
		ldesFragment.makeImmutable();
		assertTrue(ldesFragment.isImmutable());
		assertNotNull(ldesFragment.getImmutableTimestamp());
	}

	@Test
	void testEquals() {
		LdesFragment a = new LdesFragment(new ViewName("collectionName", "a"), List.of());
		LdesFragment a2 = new LdesFragment(new ViewName("collectionName", "a"), List.of());
		LdesFragment c = new LdesFragment(new ViewName("collectionName", "c"), List.of());

		assertEquals(a, a2);
		assertEquals(a2, a);
		assertNotEquals(a, c);
	}

	@Test
	void testHashCode() {
		LdesFragment a = new LdesFragment(new ViewName("collectionName", "a"), List.of());
		LdesFragment a2 = new LdesFragment(new ViewName("collectionName", "a"), List.of());
		LdesFragment c = new LdesFragment(new ViewName("collectionName", "c"), List.of());

		assertEquals(a.hashCode(), a2.hashCode());
		assertEquals(a2.hashCode(), a.hashCode());
		assertNotEquals(a.hashCode(), c.hashCode());
	}

}