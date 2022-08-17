package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.connected.distance;

import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.Tile;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.converter.TileConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.LdesFragment;

import java.util.Comparator;

public class LdesFragmentDistanceComparator implements Comparator<LdesFragment> {
	private final Tile tile;

	public LdesFragmentDistanceComparator(LdesFragment ldesFragment) {
		tile = TileConverter.fromString(ldesFragment.getFragmentInfo().getValue());
	}

	@Override
	public int compare(LdesFragment o1, LdesFragment o2) {
		Tile tile1 = TileConverter.fromString(o1.getFragmentInfo().getValue());
		Tile tile2 = TileConverter.fromString(o2.getFragmentInfo().getValue());

		Double distanceToFirstFragment = getDistanceToFragment(tile1);
		Double distanceToSecondFragment = getDistanceToFragment(tile2);

		return distanceToFirstFragment.compareTo(distanceToSecondFragment);
	}

	private double getDistanceToFragment(Tile tile1) {
		return DistanceCalculator.calculateDistance(tile.getX(), tile.getY(), tile1.getX(), tile1.getY());
	}

}
