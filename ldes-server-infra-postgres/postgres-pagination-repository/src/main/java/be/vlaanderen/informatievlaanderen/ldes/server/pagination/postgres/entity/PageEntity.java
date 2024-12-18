package be.vlaanderen.informatievlaanderen.ldes.server.pagination.postgres.entity;

import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.postgres.entity.BucketEntity;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "pages")
public class PageEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "page_id", unique = true, nullable = false, columnDefinition = "BIGINT")
	private Long id;

	@ManyToOne
	@JoinColumn(name = "bucket_id", columnDefinition = "BIGINT")
	private BucketEntity bucket;

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

	@OneToMany(mappedBy = "fromPage")
	private List<PageRelationEntity> relations;

	public PageEntity() {
	}

	public PageEntity(Long pageId) {
		this.id = pageId;
	}

	public PageEntity(Long bucketId, String partialUrl) {
		this.bucket = new BucketEntity(bucketId);
		this.immutable = false;
		this.partialUrl = partialUrl;
	}

	public Long getId() {
		return id;
	}

	public BucketEntity getBucket() {
		return bucket;
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

	public List<PageRelationEntity> getRelations() {
		return relations;
	}

	public boolean isView() {
		return bucket.getView().getComposedViewName().equals("/%s".formatted(partialUrl));
	}

	public void setId(Long id) {
		this.id = id;
	}
}
