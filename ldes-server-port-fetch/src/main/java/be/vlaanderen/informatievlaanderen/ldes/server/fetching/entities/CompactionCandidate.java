package be.vlaanderen.informatievlaanderen.ldes.server.fetching.entities;

import java.util.Objects;

public class CompactionCandidate {
	private final long id;
	private final int size;
	private final String partialUrl;
	private final long bucketId;
	private final long nextPageId;

	public CompactionCandidate(Long id, Integer size, Long nextPageId, Long bucketId, String partialUrl) {
		this.id = id;
		this.size = size;
		this.nextPageId = nextPageId;
		this.bucketId = bucketId;
		this.partialUrl = partialUrl;
	}

	public long getId() {
		return id;
	}

	public Integer getSize() {
		return size;
	}

	public String getPartialUrl() {
		return partialUrl;
	}

	public long getBucketId() {
		return bucketId;
	}

	public long getNextPageId() {
		return nextPageId;
	}

	@Override
	public String toString() {
		return "CompactionCandidate{ id='" + id +"'}";
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		CompactionCandidate that = (CompactionCandidate) o;
		return Objects.equals(id, that.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, size);
	}
}
