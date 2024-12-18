package be.vlaanderen.informatievlaanderen.ldes.server.maintenance.postgres.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "pages")
public class CompactionPageEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "page_id", unique = true, nullable = false, columnDefinition = "BIGINT")
	private Long id;

	@Column(name = "bucket_id", columnDefinition = "BIGINT")
	private Long bucketId;

	@Column(name = "immutable", nullable = false, columnDefinition = "BOOLEAN")
	private boolean immutable;

	@Column(name = "expiration", columnDefinition = "TIMESTAMP")
	private LocalDateTime expiration;

	@Column(name = "next_update_ts", columnDefinition = "TIMESTAMP")
	private LocalDateTime nextUpdateTs;

	@Column(name = "partial_url", nullable = false, unique = true)
	private String partialUrl;

	@Column(name = "is_root", nullable = false, columnDefinition = "BOOLEAN")
	private boolean isRoot;

	public CompactionPageEntity() {
	}

	public CompactionPageEntity(Long pageId) {
		this.id = pageId;
	}

	public CompactionPageEntity(Long bucketId, String partialUrl) {
		this.bucketId = bucketId;
		this.immutable = false;
		this.partialUrl = partialUrl;
	}

	public Long getId() {
		return id;
	}

	public Long getBucketId() {
		return bucketId;
	}

	public boolean isImmutable() {
		return immutable;
	}

	public LocalDateTime getExpiration() {
		return expiration;
	}

	public LocalDateTime getNextUpdateTs() {
		return nextUpdateTs;
	}

	public String getPartialUrl() {
		return partialUrl;
	}

	public void setId(Long id) {
		this.id = id;
	}
}
