package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.connected.relations;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.FragmentInfo;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.TreeRelation;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.entities.FragmentPair;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.constants.GeospatialConstants;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Stream;

import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.constants.GeospatialConstants.*;
import static org.junit.jupiter.api.Assertions.*;

class GeospatialRelationsAttributerTest {

    private final GeospatialRelationsAttributer relationsAttributer = new GeospatialRelationsAttributer();

    private final static String FRAGMENT_ID = "15/4/4";
    private final static List<String> NEIGHBOUR_FRAGMENT_IDS = List.of("15/4/3", "15/3/4", "15/5/4", "15/4/5");
    private final static List<String> OTHER_FRAGMENT_IDS = List.of("15/5/5", "15/3/3", "15/0/0", "15/10/5");

    @Test
    @DisplayName("Verify attribution of geospatial relations to neighbouring fragments")
    void when_FragmentsAreNeighbours_GeospatialRelationsAreAdded() {
        LdesFragment ldesFragment = getLdesFragment(FRAGMENT_ID);
        ldesFragment.addRelation(new TreeRelation(GEOSPARQL_AS_WKT,  "15/4/3", "<http://www.opengis.net/def/crs/OGC/1.3/CRS84> POLYGON ((-179.945068359375 85.04828470083632, -179.945068359375 85.04733631224822, -179.9560546875 85.04733631224822, -179.9560546875 85.04828470083632, -179.945068359375 85.04828470083632))", WKT_DATA_TYPE, TREE_GEOSPATIALLY_CONTAINS_RELATION));
        List<LdesFragment> neighbourFragments = convertToFragments(NEIGHBOUR_FRAGMENT_IDS);
        List<LdesFragment> otherFragments = convertToFragments(OTHER_FRAGMENT_IDS);
        List<LdesFragment> potentialNeighbours = Stream.of(List.of(ldesFragment), neighbourFragments, otherFragments)
                .flatMap(List::stream)
                .toList();

        relationsAttributer.addGeospatialRelationsToNeighbouringFragments(ldesFragment, potentialNeighbours);

        assertEquals(4, ldesFragment.getRelations().size());
        assertEquals(NEIGHBOUR_FRAGMENT_IDS, ldesFragment.getRelations().stream().map(TreeRelation::getTreeNode).toList());
        neighbourFragments.forEach(this::verifyRelationsOfNeighbourFragment);
        otherFragments.forEach(this::verifyRelationsOfOtherFragment);
    }

    private void verifyRelationsOfNeighbourFragment(LdesFragment neighbourFragment) {
        assertEquals(1, neighbourFragment.getRelations().size());
        assertEquals(FRAGMENT_ID, neighbourFragment.getRelations().get(0).getTreeNode());
    }

    private void verifyRelationsOfOtherFragment(LdesFragment otherFragment) {
        assertEquals(0, otherFragment.getRelations().size());
    }

    private List<LdesFragment> convertToFragments(List<String> fragmentIds) {
        return fragmentIds
                .stream()
                .map(this::getLdesFragment)
                .toList();
    }

    private LdesFragment getLdesFragment(String fragmentValue) {
        return new LdesFragment(fragmentValue, new FragmentInfo("", "", "", List.of(new FragmentPair(GeospatialConstants.FRAGMENT_KEY_TILE, fragmentValue))));
    }

}