package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.LdesFragmentIdentifier;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.valueobjects.FragmentPair;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Fragment.ROOT;
import static org.junit.jupiter.api.Assertions.*;

class FragmentTest {
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
		Fragment fragment = new Fragment(new LdesFragmentIdentifier(
				VIEW_NAME,
				List.of(new FragmentPair(GENERATED_AT_TIME, FRAGMENTATION_VALUE_1))));
		assertFalse(fragment.isImmutable());
		fragment.makeImmutable();
		assertTrue(fragment.isImmutable());
	}

	@Test
	void get_FragmentId() {
		Fragment fragment = new Fragment(new LdesFragmentIdentifier(
				VIEW_NAME,
				List.of(new FragmentPair(GENERATED_AT_TIME, FRAGMENTATION_VALUE_1),
						new FragmentPair(TILE, FRAGMENTATION_VALUE_2))));

		assertEquals("/collectionName/mobility-hindrances?generatedAtTime=2020-12-28T09:36:09.72Z&tile=0/0/0",
				fragment.getFragmentIdString());
		assertEquals("/collectionName/mobility-hindrances?generatedAtTime=2020-12-28T09:36:09.72Z",
				fragment.getParentIdAsString());

		fragment = new Fragment(new LdesFragmentIdentifier(
				VIEW_NAME, List.of()));
		assertEquals("/collectionName/mobility-hindrances",
				fragment.getFragmentIdString());
		assertEquals(ROOT, fragment.getParentIdAsString());

	}

	@Test
	void when_ValueIsAbsent_GetValueOfKeyReturnsOptionalEmpty() {
		Fragment fragment = new Fragment(new LdesFragmentIdentifier(VIEW_NAME,
				List.of(new FragmentPair(GENERATED_AT_TIME, FRAGMENTATION_VALUE_1),
						new FragmentPair(TILE, FRAGMENTATION_VALUE_2))));
		assertTrue(fragment.getValueOfKey("unexistingKey").isEmpty());
		assertEquals(Optional.of(FRAGMENTATION_VALUE_1), fragment.getValueOfKey(GENERATED_AT_TIME));
		assertEquals(Optional.of(FRAGMENTATION_VALUE_2), fragment.getValueOfKey(TILE));
	}

	@Test
	void when_childIsCreated_ViewIsSameAndFragmentPairsAreExtended() {
		ViewName viewName = VIEW_NAME;
		Fragment fragment = new Fragment(
				new LdesFragmentIdentifier(viewName, List.of(PARENT_FRAGMENT_PAIR)));
		Fragment child = fragment.createChild(CHILD_FRAGMENT_PAIR);
		assertEquals(List.of(PARENT_FRAGMENT_PAIR, CHILD_FRAGMENT_PAIR), child.getFragmentPairs());
		assertFalse(child.isImmutable());
		assertEquals(viewName, child.getViewName());
	}

	@Test
	void when_LdesFragmentIsMadeImmutable_ImmutableTimeStampIsSet() {
		Fragment fragment = new Fragment(new LdesFragmentIdentifier(VIEW_NAME,
				List.of(PARENT_FRAGMENT_PAIR)));
		assertFalse(fragment.isImmutable());
		fragment.makeImmutable();
		assertTrue(fragment.isImmutable());
	}

	@Test
	void when_ParentExists_Then_ReturnIdParent() {
		Fragment parent = new Fragment(new LdesFragmentIdentifier(VIEW_NAME, List.of(PARENT_FRAGMENT_PAIR)));
		Fragment child = parent.createChild(CHILD_FRAGMENT_PAIR);
		assertEquals(parent.getFragmentId(), child.getParentId().get());
		assertEquals(parent.getFragmentIdString(), child.getParentIdAsString());
	}

	@Test
	void when_ParentDoesNotExists_Then_ReturnEmpty() {
		Fragment rootFragment = new Fragment(new LdesFragmentIdentifier(VIEW_NAME, List.of()));
		assertEquals(Optional.empty(), rootFragment.getParentId());
		assertEquals(ROOT, rootFragment.getParentIdAsString());
	}

	@Test
	void testEquals() {
		Fragment a = new Fragment(new LdesFragmentIdentifier(new ViewName("collectionName", "a"), List.of()));
		Fragment a2 = new Fragment(new LdesFragmentIdentifier(new ViewName("collectionName", "a"), List.of()));
		Fragment c = new Fragment(new LdesFragmentIdentifier(new ViewName("collectionName", "c"), List.of()));

		assertEquals(a, a2);
		assertEquals(a2, a);
		assertNotEquals(a, c);
	}

	@Test
	void testHashCode() {
		Fragment a = new Fragment(new LdesFragmentIdentifier(new ViewName("collectionName", "a"), List.of()));
		Fragment a2 = new Fragment(new LdesFragmentIdentifier(new ViewName("collectionName", "a"), List.of()));
		Fragment c = new Fragment(new LdesFragmentIdentifier(new ViewName("collectionName", "c"), List.of()));

		assertEquals(a.hashCode(), a2.hashCode());
		assertEquals(a2.hashCode(), a.hashCode());
		assertNotEquals(a.hashCode(), c.hashCode());
	}

}
