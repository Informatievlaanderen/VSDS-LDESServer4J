package be.vlaanderen.informatievlaanderen.ldes.server.fetching.entities;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.TreeRelation;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Fragment;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public class CompactionCandidate {
	private final long id;
	private final int size;
	private final String partialUrl;
	private final long bucketId;
	private final long nextPageId;
	private final boolean isImmutable;
	private final LocalDateTime expiration;

	public CompactionCandidate(Long id, Integer size, Long nextPageId, Boolean isImmutable,
							   LocalDateTime expiration, Long bucketId, String partialUrl) {
		this.id = id;
		this.size = size;
		this.nextPageId = nextPageId;
		this.isImmutable = isImmutable;
		this.expiration = expiration;
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

	public boolean isCompactable() {
		return isImmutable && expiration == null;
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
