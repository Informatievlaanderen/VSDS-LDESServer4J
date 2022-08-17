package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.connected;

import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.Tile;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.config.GeospatialConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.converter.TileConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentCreator;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.entities.FragmentPair;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.constants.GeospatialConstants.FRAGMENT_KEY_TILE;

@Component
public class FragmentPathCreator {

	private final GeospatialConfig geospatialConfig;

	private final FragmentCreator fragmentCreator;

	public FragmentPathCreator(GeospatialConfig geospatialConfig, FragmentCreator fragmentCreator) {
		this.geospatialConfig = geospatialConfig;
		this.fragmentCreator = fragmentCreator;
	}

	public Set<LdesFragment> createFragmentPath(LdesFragment firstFragment, LdesFragment secondFragment) {
		Tile firstTile = TileConverter.fromString(firstFragment.getFragmentInfo().getValue());
		Tile secondTile = TileConverter.fromString(secondFragment.getFragmentInfo().getValue());
		Set<String> bucketsInBetween = getBucketsInBetween(firstFragment, secondFragment, firstTile, secondTile);
		return bucketsInBetween.stream()
				.map(bucket -> fragmentCreator.createNewFragment(Optional.empty(),
						new FragmentPair(FRAGMENT_KEY_TILE, bucket)))
				.collect(Collectors.toSet());
	}

	private Set<String> getBucketsInBetween(LdesFragment firstFragment, LdesFragment secondFragment, Tile firstTile,
			Tile secondTile) {
		Set<String> verticalBuckets = getVerticalBuckets(firstTile, secondTile);
		Set<String> horizontalBuckets = getHorizontalBuckets(firstTile, secondTile);
		Set<String> buckets = Stream.of(horizontalBuckets, verticalBuckets)
				.flatMap(Set::stream)
				.collect(Collectors.toSet());
		buckets.remove(firstFragment.getFragmentInfo().getValue());
		buckets.remove(secondFragment.getFragmentInfo().getValue());
		return buckets;
	}

	private Set<String> getHorizontalBuckets(Tile firstTile, Tile secondTile) {
		return IntStream
				.rangeClosed(Math.min(firstTile.getX(), secondTile.getX()),
						Math.max(firstTile.getX(), secondTile.getX()))
				.mapToObj(xValue -> new Tile(geospatialConfig.getMaxZoomLevel(), xValue, secondTile.getY()))
				.map(TileConverter::toString)
				.collect(Collectors.toSet());
	}

	private Set<String> getVerticalBuckets(Tile firstTile, Tile secondTile) {
		return IntStream
				.rangeClosed(Math.min(firstTile.getY(), secondTile.getY()),
						Math.max(firstTile.getY(), secondTile.getY()))
				.mapToObj(yValue -> new Tile(geospatialConfig.getMaxZoomLevel(), firstTile.getX(), yValue))
				.map(TileConverter::toString)
				.collect(Collectors.toSet());
	}
}
