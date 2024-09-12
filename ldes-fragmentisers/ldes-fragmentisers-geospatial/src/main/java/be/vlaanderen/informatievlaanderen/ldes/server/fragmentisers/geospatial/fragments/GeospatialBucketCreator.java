package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.fragments;

import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Bucket;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.ChildBucket;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.BucketRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.valueobjects.BucketDescriptorPair;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.connected.relations.TileBucketRelationsAttributer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.constants.GeospatialConstants.FRAGMENT_KEY_TILE;

public class GeospatialBucketCreator {

	private final BucketRepository bucketRepository;
	private final TileBucketRelationsAttributer tileBucketRelationsAttributer;
	private static final Logger LOGGER = LoggerFactory.getLogger(GeospatialBucketCreator.class);

	public GeospatialBucketCreator(BucketRepository bucketRepository, TileBucketRelationsAttributer tileBucketRelationsAttributer) {
		this.bucketRepository = bucketRepository;
		this.tileBucketRelationsAttributer = tileBucketRelationsAttributer;
	}

	public Bucket getOrCreateTileBucket(Bucket parentBucket, String tile, Bucket rootTileFragment) {
		final BucketDescriptorPair childDescriptorPair = new BucketDescriptorPair(FRAGMENT_KEY_TILE, tile);
		return bucketRepository
				.retrieveBucket(parentBucket.getViewName(), parentBucket.createChildDescriptor(childDescriptorPair))
				.orElseGet(() -> {
					final Bucket childBucket = bucketRepository.insertBucket(parentBucket.createChild(childDescriptorPair));
					tileBucketRelationsAttributer.addRelationsFromRootToBottom(rootTileFragment, childBucket);
					LOGGER.debug("Geospatial fragment created with id: {}", childBucket.getBucketDescriptorAsString());
					return childBucket;
				});
	}

	public Bucket getOrCreateRootBucket(Bucket parentBucket, String tile) {
		final BucketDescriptorPair childDescriptorPair = new BucketDescriptorPair(FRAGMENT_KEY_TILE, tile);
		return bucketRepository
				.retrieveBucket(parentBucket.getViewName(), parentBucket.createChildDescriptor(childDescriptorPair))
				.orElseGet(() -> {
					final Bucket childBucket = bucketRepository.insertBucket(parentBucket.createChild(childDescriptorPair));
					LOGGER.debug("Geospatial rootfragment created with id: {}", childBucket.getBucketDescriptorAsString());
					return childBucket;
				});
	}
}
