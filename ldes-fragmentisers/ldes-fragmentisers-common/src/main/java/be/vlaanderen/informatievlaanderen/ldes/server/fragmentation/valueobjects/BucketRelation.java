package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.valueobjects;

import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Bucket;

public record BucketRelation(Bucket fromBucket, Bucket toBucket, TreeRelation relation) {
	public String fromPagePartialUrl() {
		return fromBucket.createPartialUrl();
	}

	public String toPagePartialUrl() {
		return toBucket.createPartialUrl();
	}
}
