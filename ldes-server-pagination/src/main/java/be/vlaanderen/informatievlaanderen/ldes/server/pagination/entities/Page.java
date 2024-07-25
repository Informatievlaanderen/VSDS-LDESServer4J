package be.vlaanderen.informatievlaanderen.ldes.server.pagination.entities;

import be.vlaanderen.informatievlaanderen.ldes.server.pagination.valueobjects.PartialUrl;

public class Page {
	private final long id;
	private final long bucketId;
	private final PartialUrl partialUrl;
	private final int pageSize;
	private int assignedMemberCount;

	public Page(long id, long bucketId, PartialUrl partialUrl, int pageSize) {
		this(id, bucketId, partialUrl, pageSize, 0);
	}

	public Page(long id, long bucketId, PartialUrl partialUrl, int pageSize, int assignedMemberCount) {
		this.id = id;
		this.bucketId = bucketId;
		this.partialUrl = partialUrl;
		this.pageSize = pageSize;
		this.assignedMemberCount = assignedMemberCount;
	}

	public Page(long id, long bucketId, String partialUrl, int pageSize) {
		this(id, bucketId, partialUrl, pageSize, 0);
	}

	public Page(long id, long bucketId, String partialUrl, int pageSize, int assignedMemberCount) {
		this.id = id;
		this.bucketId = bucketId;
		this.partialUrl = PartialUrl.fromUrl(partialUrl);
		this.pageSize = pageSize;
		this.assignedMemberCount = assignedMemberCount;
	}

	public long getId() {
		return id;
	}

	public long getBucketId() {
		return bucketId;
	}

	public boolean isFull() {
		return getAvailableMemberSpace() == 0;
	}

	public int getAvailableMemberSpace() {
		return pageSize - assignedMemberCount;
	}

	public void incrementAssignedMemberCount(int assignedMemberCount) {
		this.assignedMemberCount += assignedMemberCount;
	}

	public int getMaximumMemberCount() {
		return pageSize;
	}

	public static Page createWithPartialUrl(long id, long bucketId, String partialUrl, int assignedMemberCount, int maximumMemberCount) {
		return new Page(id, bucketId, PartialUrl.fromUrl(partialUrl), maximumMemberCount, assignedMemberCount);
	}

	public PartialUrl createChildPartialUrl() {
		return partialUrl.createChild();
	}

	public boolean isNumberLess() {
		return partialUrl.isNumberLess();
	}

	@Override
	public final boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Page page)) return false;

		return id == page.id;
	}

	@Override
	public int hashCode() {
		return Long.hashCode(id);
	}
}
