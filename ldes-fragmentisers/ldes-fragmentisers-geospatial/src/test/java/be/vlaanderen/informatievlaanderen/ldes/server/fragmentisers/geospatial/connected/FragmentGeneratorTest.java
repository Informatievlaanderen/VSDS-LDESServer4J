package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.connected;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.FragmentInfo;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.entities.FragmentPair;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.constants.GeospatialConstants;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class FragmentGeneratorTest {

    private final static String FRAGMENT_ID = "15/4/4";
    private final static String CLOSEST_FRAGMENT_ID = "15/1/3";
    private final static List<String> FRAGMENTS_BETWEEN_SOURCE_AND_CLOSEST = List.of("15/4/3","15/3/3","15/2/3");
    private final static List<String> OTHER_FRAGMENT_IDS = List.of("15/15/4", "15/4/10", "15/7/7");
    private final FragmentPathCreator fragmentPathCreator = mock(FragmentPathCreator.class);
    private final ClosestFragmentDiscoverer closestFragmentDiscoverer = new ClosestFragmentDiscoverer();
    private final FragmentGenerator fragmentGenerator = new FragmentGenerator(closestFragmentDiscoverer, fragmentPathCreator);

    @Test
    @DisplayName("Verify generation of fragments between original fragment and closest fragment")
    void when_FragmentAndCollectionOfAvailable_FragmentsOnPathToClosestFragmentAreReturned() {
        LdesFragment ldesFragment = getLdesFragment(FRAGMENT_ID);
        LdesFragment closestFragment = getLdesFragment(CLOSEST_FRAGMENT_ID);
        List<LdesFragment> availableFragments = getAvailableFragments(closestFragment);
        when(fragmentPathCreator.createFragmentPath(ldesFragment, closestFragment)).thenReturn(getFragmentsInBetween());

        List<LdesFragment> fragmentsInPath = fragmentGenerator.generateFragmentPathToClosestFragment(ldesFragment, availableFragments);

        assertTrue(fragmentsInPath.contains(ldesFragment));
        assertTrue(fragmentsInPath.contains(closestFragment));
        Set<String> expectedFragmentIdsInPath = getExpectedFragmentIdsInPath();
        assertEquals(expectedFragmentIdsInPath, getActualFragmentIdsInPath(fragmentsInPath));
    }

    private Set<String> getActualFragmentIdsInPath(List<LdesFragment> fragmentsInPath) {
        return fragmentsInPath
                .stream()
                .map(LdesFragment::getFragmentId)
                .collect(Collectors.toSet());
    }

    private Set<String> getExpectedFragmentIdsInPath() {
        return Stream.of(List.of(FRAGMENT_ID, CLOSEST_FRAGMENT_ID), FRAGMENTS_BETWEEN_SOURCE_AND_CLOSEST)
                .flatMap(List::stream)
                .collect(Collectors.toSet());
    }

    private Set<LdesFragment> getFragmentsInBetween() {
        return FRAGMENTS_BETWEEN_SOURCE_AND_CLOSEST
                .stream()
                .map(this::getLdesFragment)
                .collect(Collectors.toSet());
    }

    private List<LdesFragment> getAvailableFragments(LdesFragment closestFragment) {
        List<LdesFragment> availableFragments = OTHER_FRAGMENT_IDS
                .stream()
                .map(this::getLdesFragment)
                .collect(Collectors.toList());
        availableFragments.add(closestFragment);
        return availableFragments;
    }

    private LdesFragment getLdesFragment(String fragmentValue) {
        return new LdesFragment(fragmentValue, new FragmentInfo("", List.of(new FragmentPair(GeospatialConstants.FRAGMENT_KEY_TILE, fragmentValue))));
    }

}