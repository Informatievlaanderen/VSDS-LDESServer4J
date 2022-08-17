package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.connected.distance;

import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.Tile;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.converter.TileConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;

import java.util.function.Predicate;

public class AdjacentLdesFragmentFilter implements Predicate<LdesFragment> {

	private final LdesFragment originLdesFragment;

	public AdjacentLdesFragmentFilter(LdesFragment ldesFragment) {
		this.originLdesFragment = ldesFragment;
	}

	/**
	 * Determines if the LdesFragment is adjacent to the origin LdesFragment
	 *
	 * @param ldesFragment
	 *            the input argument
	 * @return true when the distance between the two LdesFragments is 1, i.d. they
	 *         are adjacent.
	 */
	@Override
	public boolean test(LdesFragment ldesFragment) {
		return distanceBetween(originLdesFragment, ldesFragment) == 1.0;
	}

	private double distanceBetween(LdesFragment firstFragment, LdesFragment secondFragment) {
		Tile firstTile = TileConverter.fromString(firstFragment.getFragmentInfo().getValue());
		Tile secondTile = TileConverter.fromString(secondFragment.getFragmentInfo().getValue());
		return DistanceCalculator.calculateDistance(firstTile.getX(), firstTile.getY(), secondTile.getX(),
				secondTile.getY());
	}
}
