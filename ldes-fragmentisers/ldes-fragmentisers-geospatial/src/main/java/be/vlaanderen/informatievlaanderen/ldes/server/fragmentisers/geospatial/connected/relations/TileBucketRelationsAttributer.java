package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.connected.relations;

import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Bucket;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.valueobjects.BucketRelation;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.valueobjects.BucketRelationCreatedEvent;
import org.springframework.context.ApplicationEventPublisher;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.ServerConstants.DEFAULT_BUCKET_STRING;
import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.constants.GeospatialConstants.FRAGMENT_KEY_TILE;

public class TileBucketRelationsAttributer {

	private final GeospatialRelationsAttributer relationsAttributer = new GeospatialRelationsAttributer();
	private final ApplicationEventPublisher applicationEventPublisher;

	public TileBucketRelationsAttributer(ApplicationEventPublisher applicationEventPublisher) {
		this.applicationEventPublisher = applicationEventPublisher;
	}

	public void addRelationsFromRootToBottom(Bucket rootBucket, Bucket tileBucket) {
		boolean isDefaultBucket = tileBucket.getValueForKey(FRAGMENT_KEY_TILE).orElse("").equals(DEFAULT_BUCKET_STRING);
		BucketRelation bucketRelation = isDefaultBucket ? BucketRelation.createGenericRelation(rootBucket, tileBucket) : relationsAttributer.createRelationBetween(rootBucket, tileBucket);
		applicationEventPublisher.publishEvent(new BucketRelationCreatedEvent(bucketRelation));
	}
}
