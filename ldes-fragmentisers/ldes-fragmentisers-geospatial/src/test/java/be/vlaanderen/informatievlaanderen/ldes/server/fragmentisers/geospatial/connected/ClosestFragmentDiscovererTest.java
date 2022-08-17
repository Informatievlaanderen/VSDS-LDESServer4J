package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.connected;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.FragmentInfo;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.entities.FragmentPair;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.constants.GeospatialConstants;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.exceptions.NoClosestFragmentFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ClosestFragmentDiscovererTest {

	private final ClosestFragmentDiscoverer closestFragmentDiscoverer = new ClosestFragmentDiscoverer();

	@Test
	@DisplayName("ClosestFragmentDiscoverer returns Closest Fragment")
	void when_ListOfAvailableFragmentsContainsFragments_ClosestFragmentIsReturned() {
		LdesFragment ldesFragment = getLdesFragment("15/4/4");
		LdesFragment expectedClosestFragment = getLdesFragment("15/5/5");
		LdesFragment fragment1 = getLdesFragment("15/6/5");
		LdesFragment fragment2 = getLdesFragment("15/6/4");
		LdesFragment fragment3 = getLdesFragment("15/4/6");
		List<LdesFragment> availableFragments = List.of(fragment1, fragment2, fragment3, expectedClosestFragment);

		LdesFragment actualClosestFragment = closestFragmentDiscoverer.getClosestFragment(ldesFragment,
				availableFragments);

		assertEquals(expectedClosestFragment, actualClosestFragment);
	}

	@Test
	@DisplayName("ClosestFragmentDiscoverer throws NoClosestFragmentFoundException")
	void when_ListOfAvailableFragmentsIsEmpty_NoClosestFragmentCanBeFound() {
		LdesFragment ldesFragment = getLdesFragment("15/4/4");
		List<LdesFragment> availableFragments = List.of();

		NoClosestFragmentFoundException noClosestFragmentFoundException = assertThrows(
				NoClosestFragmentFoundException.class,
				() -> closestFragmentDiscoverer.getClosestFragment(ldesFragment, availableFragments));
		assertEquals("Could not find closest fragment to fragment 15/4/4 in collection ",
				noClosestFragmentFoundException.getMessage());
	}

	private LdesFragment getLdesFragment(String fragmentValue) {
		return new LdesFragment(fragmentValue,
				new FragmentInfo("", List.of(new FragmentPair(GeospatialConstants.FRAGMENT_KEY_TILE, fragmentValue))));
	}

}