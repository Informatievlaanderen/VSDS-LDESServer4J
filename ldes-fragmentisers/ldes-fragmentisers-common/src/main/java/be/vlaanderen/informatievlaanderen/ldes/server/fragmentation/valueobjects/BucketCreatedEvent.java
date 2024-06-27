package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.valueobjects;

import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Bucket;

public record BucketCreatedEvent(BucketRelation bucketRelation) {
	Bucket parentBucket() {
		return bucketRelation.fromBucket();
	}

	Bucket childBucket() {
		return bucketRelation.toBucket();
	}
}
