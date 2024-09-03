package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial;

import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.FragmentationStrategy;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.FragmentationStrategyDecorator;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Bucket;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.BucketisedMember;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.FragmentationMember;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.bucketising.GeospatialBucketiser;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.fragments.GeospatialBucketCreator;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Collection;
import java.util.List;
import java.util.Set;

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
	                                       ObservationRegistry observationRegistry,
	                                       ApplicationEventPublisher applicationEventPublisher) {
		super(fragmentationStrategy, applicationEventPublisher);
		this.geospatialBucketiser = geospatialBucketiser;
		this.bucketCreator = bucketCreator;
		this.observationRegistry = observationRegistry;
	}

	@Override
	public List<BucketisedMember> addMemberToBucket(Bucket parentBucket, FragmentationMember member,
	                                                Observation parentObservation) {
		Observation geospatialFragmentationObservation = Observation
				.createNotStarted("geospatial bucketisation", observationRegistry)
				.parentObservation(parentObservation)
				.start();
		setRootTileBucket(parentBucket);

		Set<String> tiles = geospatialBucketiser.bucketise(member.getSubject(), member.getVersionModel());

		List<Bucket> buckets = tiles
				.stream()
				.map(tile -> {
					if (tile.equals(DEFAULT_BUCKET_STRING)) {
						return bucketCreator.getOrCreateTileBucket(parentBucket, tile, parentBucket);
					} else {
						return bucketCreator.getOrCreateTileBucket(parentBucket, tile, rootTileBucket);
					}
				}).toList();

		List<BucketisedMember> members = buckets
				.parallelStream()
				.map(bucket -> super.addMemberToBucket(bucket, member, geospatialFragmentationObservation))
				.flatMap(Collection::stream)
				.toList();
		geospatialFragmentationObservation.stop();
		return members;
	}


	private void setRootTileBucket(Bucket parentBucket) {
		if (rootTileBucket == null) {
			rootTileBucket = bucketCreator.getOrCreateRootBucket(parentBucket, FRAGMENT_KEY_TILE_ROOT);
			super.addRelationFromParentToChild(parentBucket, rootTileBucket);
		}
	}
}
