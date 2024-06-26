package be.vlaanderen.informatievlaanderen.ldes.server.pagination.postgres.entity;

import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.postgres.entity.BucketEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.postgres.entity.MemberEntity;
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
	@JoinColumn(name = "bucket_id", nullable = false, columnDefinition = "BIGINT")
	private BucketEntity bucket;

	@Column(name = "expiration", columnDefinition = "TIMESTAMP")
	private LocalDateTime expiration;

	@Column(name = "partial_url", nullable = false, unique = true)
	private String partialUrl;

	@OneToMany(mappedBy = "relation_id")
	private List<RelationEntity> relations;

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(
			name = "page_members",
			joinColumns = @JoinColumn(name = "page_id"),
			inverseJoinColumns = @JoinColumn(name = "member_id")
	)
	private List<MemberEntity> members;

	public PageEntity() {
	}

	public PageEntity(Long id, BucketEntity bucket, LocalDateTime expiration, String partialUrl) {
		this.id = id;
		this.bucket = bucket;
		this.expiration = expiration;
		this.partialUrl = partialUrl;
	}
}
