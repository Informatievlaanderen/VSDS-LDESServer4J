package be.vlaanderen.informatievlaanderen.ldes.server.pagination.entities;

import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Bucket;

import java.time.LocalDateTime;

public record Page(long pageId, Bucket bucket, LocalDateTime expiration, String pageNumberUrlPart) {

	public Page(Bucket bucket, LocalDateTime expiration, String pageNumberUrlPart) {
		this(0, bucket, expiration, pageNumberUrlPart);
	}

	public long bucketId() {
		return bucket.getBucketId();
	}


	public String partialUrl() {
		return bucket.createPartialUrl() + "&" + pageNumberUrlPart;
	}

	public static Page createPageWithPartialUrl(long pageId, Bucket bucket, LocalDateTime expiration, String partialUrl) {
		String pageNumberUrlPart = partialUrl.replace(bucket.createPartialUrl(), "");
		if (!pageNumberUrlPart.isEmpty()) {
			pageNumberUrlPart = pageNumberUrlPart.substring(1);
		}
		return new Page(pageId, bucket, expiration, pageNumberUrlPart);
	}

	public Page createRelatedPage() {
		final String pageNumberRegex = "&pageNumber=";
		final int relatedPageNumber = Integer.parseInt(pageNumberUrlPart.replace(pageNumberRegex, ""));
		return new Page(bucket, null, pageNumberRegex + relatedPageNumber);
	}

	public static Page fromBucket(Bucket bucket, LocalDateTime expiration) {
		return new Page(bucket, expiration, "");
	}

	public static Page fromBucket(Bucket bucket) {
		return fromBucket(bucket, null);
	}

}
