package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.FragmentInfo;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.valueobjects.FragmentPair;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LdesFragmentTest {
	private static final String COLLECTION_NAME = "mobility-hindrances";
	private static final String VIEW_NAME = "mobility-hindrances";
	private static final String FRAGMENTATION_VALUE_1 = "2020-12-28T09:36:09.72Z";
	private static final String FRAGMENT_ID = "http://localhost:8080/mobility-hindrances?generatedAtTime="
			+ FRAGMENTATION_VALUE_1;
	private static final String TIMESTAMP_PATH = "http://www.w3.org/ns/prov#generatedAtTime";

	@Test
	@DisplayName("Test if fragment is immutable or not")
	void when_LdesFragmentIsImmutable_IsImmutableReturnsTrue() {
		LdesFragment ldesFragment = new LdesFragment(
				new FragmentInfo(VIEW_NAME,
						List.of(new FragmentPair(TIMESTAMP_PATH, FRAGMENTATION_VALUE_1))));
		assertFalse(ldesFragment.isImmutable());
		ldesFragment.setImmutable(true);
		assertTrue(ldesFragment.isImmutable());
	}

	@Test
	@DisplayName("Test current number of members")
	void when_CurrentNumberOfMembersIsRequested_LdesFragmentsReturnsNumberOfMembers() {
		LdesFragment ldesFragment = new LdesFragment(
				new FragmentInfo(VIEW_NAME,
						List.of(new FragmentPair(TIMESTAMP_PATH, FRAGMENTATION_VALUE_1))));

		assertEquals(0, ldesFragment.getCurrentNumberOfMembers());
		ldesFragment.addMember("some_id");
		assertEquals(1, ldesFragment.getCurrentNumberOfMembers());
		ldesFragment.addMember("some_id_2");
		ldesFragment.addMember("some_id_3");
		assertEquals(3, ldesFragment.getCurrentNumberOfMembers());
	}
}