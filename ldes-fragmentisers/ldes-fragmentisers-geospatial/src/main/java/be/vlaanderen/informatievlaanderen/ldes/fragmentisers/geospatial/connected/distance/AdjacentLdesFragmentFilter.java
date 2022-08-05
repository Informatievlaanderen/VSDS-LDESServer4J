package be.vlaanderen.informatievlaanderen.ldes.fragmentisers.geospatial.connected.distance;

import be.vlaanderen.informatievlaanderen.ldes.fragmentisers.geospatial.Tile;
import be.vlaanderen.informatievlaanderen.ldes.fragmentisers.geospatial.converter.TileConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.LdesFragment;

import java.util.function.Predicate;

public class AdjacentLdesFragmentFilter implements Predicate<LdesFragment> {

    private final LdesFragment originLdesFragment;

    public AdjacentLdesFragmentFilter(LdesFragment ldesFragment) {
        this.originLdesFragment = ldesFragment;
    }

    @Override
    public boolean test(LdesFragment ldesFragment) {
        return distanceBetween(originLdesFragment, ldesFragment) == 1.0;
    }

    private double distanceBetween(LdesFragment firstFragment, LdesFragment secondFragment) {
        Tile firstTile = TileConverter.fromString(firstFragment.getFragmentInfo().getValue());
        Tile secondTile = TileConverter.fromString(secondFragment.getFragmentInfo().getValue());
        return DistanceCalculator.calculateDistance(firstTile.getX(), firstTile.getY(), secondTile.getX(), secondTile.getY());
    }
}
