package be.vlaanderen.informatievlaanderen.ldes.server.pagination.entities;

import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Bucket;
import be.vlaanderen.informatievlaanderen.ldes.server.pagination.valueobjects.PageNumber;

import java.time.LocalDateTime;
import java.util.Optional;

public final class Page {
	private final long pageId;
	private final Bucket bucket;
	private final PageNumber pageNumber;

	public Page(long pageId, Bucket bucket, LocalDateTime expiration, PageNumber pageNumber) {
		this.pageId = pageId;
		this.bucket = bucket;
		this.pageNumber = pageNumber;
	}

	public Page(Bucket bucket, LocalDateTime expiration, PageNumber pageNumber) {
		this(0, bucket, expiration, pageNumber);
	}

	public long bucketId() {
		return bucket.getBucketId();
	}


	public String partialUrl() {
		return bucket.createPartialUrl() + getPageNumber().map(PageNumber::asUrlPart).orElse("");
	}

	public static Page createPageWithPartialUrl(long pageId, Bucket bucket, LocalDateTime expiration, String partialUrl) {
		String pageNumberUrlPart = partialUrl.replace(bucket.createPartialUrl(), "");
		if (!pageNumberUrlPart.isEmpty()) {
			final int pageNumber = Integer.parseInt(pageNumberUrlPart.substring(1).replace("&pageNumber=", ""));
			return new Page(pageId, bucket, expiration, new PageNumber(pageNumber));

		}
		return Page.createNumberLessPage(pageId, bucket, expiration);

	}

	public Page createChildPage() {
		return new Page(bucket, null, getPageNumber().map(PageNumber::increment).orElseGet(PageNumber::startPageNumber));
	}

	public static Page fromBucket(Bucket bucket, LocalDateTime expiration) {
		return new Page(bucket, expiration, null);
	}

	public static Page fromBucket(Bucket bucket) {
		return fromBucket(bucket, null);
	}

	public long getPageId() {
		return pageId;
	}

	public Bucket getBucket() {
		return bucket;
	}


	public Optional<PageNumber> getPageNumber() {
		return Optional.ofNullable(pageNumber);
	}

	public boolean isNumberLess() {
		return getPageNumber().isEmpty();
	}

	public static Page createNumberLessPage(long id, Bucket bucket, LocalDateTime expiration) {
		return new Page(id, bucket, expiration, null);
	}

	public static Page createNumberLessPage(Bucket bucket, LocalDateTime expiration) {
		return new Page(0L, bucket, expiration, null);
	}


	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Page page)) return false;

		return pageId == page.pageId;
	}

	@Override
	public int hashCode() {
		return Long.hashCode(pageId);
	}
}
