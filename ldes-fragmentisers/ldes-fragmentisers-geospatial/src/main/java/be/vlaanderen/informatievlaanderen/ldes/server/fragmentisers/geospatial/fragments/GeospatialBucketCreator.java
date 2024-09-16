package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.fragments;

import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Bucket;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.valueobjects.BucketDescriptorPair;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.connected.relations.TileBucketRelationsAttributer;

import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.constants.GeospatialConstants.FRAGMENT_KEY_TILE;

public class GeospatialBucketCreator {

	private final TileBucketRelationsAttributer tileBucketRelationsAttributer;

	public GeospatialBucketCreator(TileBucketRelationsAttributer tileBucketRelationsAttributer) {
		this.tileBucketRelationsAttributer = tileBucketRelationsAttributer;
	}

	public Bucket getOrCreateTileBucket(Bucket parentBucket, String tile, Bucket rootTileFragment) {
		final BucketDescriptorPair childDescriptorPair = new BucketDescriptorPair(FRAGMENT_KEY_TILE, tile);
		final Bucket childBucket = parentBucket.createChild(childDescriptorPair);
		tileBucketRelationsAttributer.addRelationsFromRootToBottom(rootTileFragment, childBucket);
		return childBucket;
	}

	public Bucket getOrCreateRootBucket(Bucket parentBucket, String tile) {
		final BucketDescriptorPair childDescriptorPair = new BucketDescriptorPair(FRAGMENT_KEY_TILE, tile);
		return parentBucket.createChild(childDescriptorPair);
	}
}
