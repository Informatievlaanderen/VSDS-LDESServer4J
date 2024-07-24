package be.vlaanderen.informatievlaanderen.ldes.server.pagination.entities;

import be.vlaanderen.informatievlaanderen.ldes.server.pagination.valueobjects.Bucket;
import be.vlaanderen.informatievlaanderen.ldes.server.pagination.valueobjects.PageNumber;

import static be.vlaanderen.informatievlaanderen.ldes.server.pagination.valueobjects.PageNumber.PAGE_NUMBER;

public class Page {
	private final long id;
	private final String viewNameUrlPrefix;
	private final Bucket bucket;
	private final PageNumber pageNumber;
	private int assignedMemberCount;
	private final int maximumMemberCount;

	public Page(long id, String viewNameUrlPrefix, Bucket bucket, PageNumber pageNumber, int assignedMemberCount, int maximumMemberCount) {
		this.id = id;
		this.viewNameUrlPrefix = viewNameUrlPrefix;
		this.bucket = bucket;
		this.pageNumber = pageNumber;
		this.assignedMemberCount = assignedMemberCount;
		this.maximumMemberCount = maximumMemberCount;
	}

	public long getId() {
		return id;
	}

	public long getBucketId() {
		return bucket.id();
	}

	public String getPartialUrl() {
		return viewNameUrlPrefix + "?" + (bucket.descriptor().isEmpty() ? "" : bucket.descriptor() + "&") + pageNumber.asString();
	}

	public boolean isFull() {
		return getAvailableMemberSpace() == 0;
	}

	public int getAvailableMemberSpace() {
		return maximumMemberCount - assignedMemberCount;
	}

	public int getAssignedMemberCount() {
		return assignedMemberCount;
	}

	public void setAssignedMemberCount(int assignedMemberCount) {
		this.assignedMemberCount = assignedMemberCount;
	}

	public int getMaximumMemberCount() {
		return maximumMemberCount;
	}

	public static Page createWithPartialUrl(long id, long bucketId, String partialUrl, int assignedMemberCount, int maximumMemberCount) {
		final String[] mainPartialUrlParts = partialUrl.split("\\?");
		final String urlPrefix = mainPartialUrlParts[0];
		final String extendedBucketDescriptor = mainPartialUrlParts.length == 2 ? mainPartialUrlParts[1] : "";
		final String[] descriptorParts = extendedBucketDescriptor.split(PAGE_NUMBER + "=");
		final PageNumber pageNumber = descriptorParts.length == 2 ? new PageNumber(Integer.parseInt(descriptorParts[1])) : null;
		return new Page(id, urlPrefix, new Bucket(bucketId, descriptorParts[0]), pageNumber, assignedMemberCount, maximumMemberCount);
	}


	public String createChildPartialUrl() {
		final PageNumber childPageNumber = pageNumber == null ? new PageNumber(1) : pageNumber.increment();
		return viewNameUrlPrefix + "?" + (bucket.descriptor().isEmpty() ? "" : bucket.descriptor() + "&") + childPageNumber.asString();
	}

	public boolean isNumberLess() {
		return pageNumber == null;
	}
}
