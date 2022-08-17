package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial;

import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.connected.distance.LdesFragmentDistanceComparator;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.FragmentInfo;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.entities.FragmentPair;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LdesFragmentDistanceComparatorTest {

    String ROOT_LOCATION = "15/0/0";
    String FIRST_LOCATION = "15/1/0";
    String SECOND_LOCATION = "15/1/5";

    @Test
    void testComparator() {
        LdesFragment rootFragment = createFragment(ROOT_LOCATION);
        LdesFragment firstFragment = createFragment(FIRST_LOCATION);
        LdesFragment secondFragment = createFragment(SECOND_LOCATION);

        Optional<LdesFragment> closestFragment = Stream.of(firstFragment, secondFragment)
                .min(new LdesFragmentDistanceComparator(rootFragment));

        assertEquals(firstFragment, closestFragment.get());
    }

    private LdesFragment createFragment(String tile) {
        return new LdesFragment("x", new FragmentInfo("", List.of(new FragmentPair("tile", tile))));
    }
}