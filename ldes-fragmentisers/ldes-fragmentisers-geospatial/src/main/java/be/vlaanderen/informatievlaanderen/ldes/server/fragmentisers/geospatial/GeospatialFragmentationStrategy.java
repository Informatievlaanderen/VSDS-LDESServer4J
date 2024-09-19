package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial;

import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.FragmentationStrategy;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.FragmentationStrategyDecorator;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Bucket;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.FragmentationMember;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.bucketising.GeospatialBucketiser;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.fragments.GeospatialBucketCreator;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.ServerConstants.DEFAULT_BUCKET_STRING;
import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.constants.GeospatialConstants.FRAGMENT_KEY_TILE_ROOT;

public class GeospatialFragmentationStrategy extends FragmentationStrategyDecorator {
	public static final String GEOSPATIAL_FRAGMENTATION = "GeospatialFragmentation";
	private final GeospatialBucketiser geospatialBucketiser;
	private final GeospatialBucketCreator bucketCreator;
	private final ObservationRegistry observationRegistry;

	private Bucket rootTileBucket = null;

	public GeospatialFragmentationStrategy(FragmentationStrategy fragmentationStrategy,
	                                       GeospatialBucketiser geospatialBucketiser,
	                                       GeospatialBucketCreator bucketCreator,
	                                       ObservationRegistry observationRegistry) {
		super(fragmentationStrategy);
		this.geospatialBucketiser = geospatialBucketiser;
		this.bucketCreator = bucketCreator;
		this.observationRegistry = observationRegistry;
	}

	@Override
	public void addMemberToBucket(Bucket parentBucket, FragmentationMember member, Observation parentObservation) {
		Observation geospatialFragmentationObservation = Observation
				.createNotStarted("geospatial bucketisation", observationRegistry)
				.parentObservation(parentObservation)
				.start();
		setRootTileBucket(parentBucket);

		geospatialBucketiser.createTiles(member.getSubject(), member.getVersionModel())
				.stream()
				.map(tile -> {
					if (tile.equals(DEFAULT_BUCKET_STRING)) {
						return bucketCreator.createTileBucket(parentBucket, tile, parentBucket);
					} else {
						return bucketCreator.createTileBucket(parentBucket, tile, rootTileBucket);
					}
				})
				.parallel()
				.forEach(bucket -> super.addMemberToBucket(bucket, member, geospatialFragmentationObservation));
		geospatialFragmentationObservation.stop();
	}

	private void setRootTileBucket(Bucket parentBucket) {
		if (rootTileBucket == null) {
			rootTileBucket = bucketCreator.getOrCreateRootBucket(parentBucket, FRAGMENT_KEY_TILE_ROOT);
			parentBucket.addChildBucket(rootTileBucket.withGenericRelation());
		}
	}
}
