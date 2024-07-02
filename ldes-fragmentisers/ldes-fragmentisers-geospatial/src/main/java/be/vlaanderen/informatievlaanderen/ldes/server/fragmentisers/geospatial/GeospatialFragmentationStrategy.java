package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial;

import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.FragmentationStrategy;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.FragmentationStrategyDecorator;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Bucket;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.BucketisedMember;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Fragment;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.FragmentationMember;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.FragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.bucketising.GeospatialBucketiser;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.fragments.GeospatialBucketCreator;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.fragments.GeospatialFragmentCreator;
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
	private final GeospatialFragmentCreator fragmentCreator;
	private final GeospatialBucketCreator bucketCreator;
	private final ObservationRegistry observationRegistry;

	private Fragment rootTileFragment = null;
	private Bucket rootTileBucket = null;

	public GeospatialFragmentationStrategy(FragmentationStrategy fragmentationStrategy,
	                                       GeospatialBucketiser geospatialBucketiser,
	                                       GeospatialFragmentCreator fragmentCreator,
	                                       GeospatialBucketCreator bucketCreator,
	                                       ObservationRegistry observationRegistry,
	                                       FragmentRepository fragmentRepository,
	                                       ApplicationEventPublisher applicationEventPublisher) {
		super(fragmentationStrategy, fragmentRepository, applicationEventPublisher);
		this.geospatialBucketiser = geospatialBucketiser;
		this.fragmentCreator = fragmentCreator;
		this.bucketCreator = bucketCreator;
		this.observationRegistry = observationRegistry;
	}

	@Override
	public List<BucketisedMember> addMemberToFragment(Fragment parentFragment, FragmentationMember member,
													  Observation parentObservation) {
		Observation geospatialFragmentationObservation = Observation.createNotStarted("geospatial fragmentation",
				observationRegistry)
				.parentObservation(parentObservation)
				.start();
		getRootTileFragment(parentFragment);

		Set<String> tiles = geospatialBucketiser.bucketise(member.getSubject(), member.getVersionModel());

		List<Fragment> fragments = tiles
				.stream()
				.map(tile -> {
					if (tile.equals(DEFAULT_BUCKET_STRING)) {
						return fragmentCreator.getOrCreateTileFragment(parentFragment, tile, parentFragment);
					} else {
						return fragmentCreator.getOrCreateTileFragment(parentFragment, tile, rootTileFragment);
					}
				}).toList();

		List<BucketisedMember> members = fragments
				.parallelStream()
				.map(ldesFragment -> super.addMemberToFragment(ldesFragment, member, geospatialFragmentationObservation))
				.flatMap(Collection::stream)
				.toList();
		geospatialFragmentationObservation.stop();
		return members;
	}

	@Override
	public List<BucketisedMember> addMemberToBucket(Bucket parentBucket, FragmentationMember member,
	                                                Observation parentObservation) {
		Observation geospatialFragmentationObservation = Observation
				.createNotStarted("geospatial bucketisation", observationRegistry)
				.parentObservation(parentObservation)
				.start();
		getRootTileBucket(parentBucket);

		Set<String> tiles = geospatialBucketiser.bucketise(member.getSubject(), member.getVersionModel());

		List<Bucket> buckets = tiles
				.stream()
				.map(tile -> {
					if (tile.equals(DEFAULT_BUCKET_STRING)) {
						return bucketCreator.getOrCreateTileFragment(parentBucket, tile, parentBucket);
					} else {
						return bucketCreator.getOrCreateTileFragment(parentBucket, tile, rootTileBucket);
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

	private void getRootTileFragment(Fragment parentFragment) {
		if (rootTileFragment == null) {
			Fragment tileRootFragment = fragmentCreator.getOrCreateRootFragment(parentFragment, FRAGMENT_KEY_TILE_ROOT);
			super.addRelationFromParentToChild(parentFragment, tileRootFragment);
			rootTileFragment = tileRootFragment;
		}
	}

	private void getRootTileBucket(Bucket parentBucket) {
		if (rootTileBucket == null) {
			rootTileBucket = bucketCreator.getOrCreateRootBucket(parentBucket, FRAGMENT_KEY_TILE_ROOT);
			super.addRelationFromParentToChild(parentBucket, rootTileBucket);
		}
	}
}
