package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.FragmentInfo;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.valueobjects.FragmentPair;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

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
		ldesFragment.setImmutable(true);
		assertTrue(ldesFragment.isImmutable());
	}

	@Test
	@DisplayName("Test current number of members")
	void when_CurrentNumberOfMembersIsRequested_LdesFragmentsReturnsNumberOfMembers() {
		LdesFragment ldesFragment = new LdesFragment(
				new FragmentInfo(VIEW_NAME,
						List.of(new FragmentPair(GENERATED_AT_TIME, FRAGMENTATION_VALUE_1))));

		assertEquals(0, ldesFragment.getCurrentNumberOfMembers());
		ldesFragment.addMember("some_id");
		assertEquals(1, ldesFragment.getCurrentNumberOfMembers());
		ldesFragment.addMember("some_id_2");
		ldesFragment.addMember("some_id_3");
		assertEquals(3, ldesFragment.getCurrentNumberOfMembers());
	}

	@Test
	void when_ChildIsCreated_ChildHasExtendedFragmentPairs() {
		LdesFragment ldesFragment = new LdesFragment(
				new FragmentInfo(VIEW_NAME,
						List.of(new FragmentPair(GENERATED_AT_TIME, FRAGMENTATION_VALUE_1))));

		LdesFragment child = ldesFragment.createChild(new FragmentPair("a", "b"));
		assertEquals("/mobility-hindrances?generatedAtTime=2020-12-28T09:36:09.72Z&a=b", child.getFragmentId());
		assertFalse(child.isImmutable());
		assertEquals(0, child.getMemberIds().size());
		assertEquals(0, child.getRelations().size());
		assertEquals(List.of(new FragmentPair(GENERATED_AT_TIME, FRAGMENTATION_VALUE_1), new FragmentPair("a", "b")),
				child.getFragmentInfo().getFragmentPairs());
	}
}