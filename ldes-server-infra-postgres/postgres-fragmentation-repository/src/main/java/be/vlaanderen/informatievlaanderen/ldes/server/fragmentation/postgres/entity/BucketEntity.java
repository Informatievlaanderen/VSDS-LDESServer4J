package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.postgres.entity;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.view.entity.ViewEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.postgres.entity.MemberEntity;
import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.List;

@Entity
@Table(name = "buckets", indexes = @Index(unique = true, columnList = "view_id,bucket"))
public class BucketEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "bucket_id", nullable = false, unique = true, columnDefinition = "BIGINT")
	private Long bucketId;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@OnDelete(action = OnDeleteAction.CASCADE)
	@JoinColumn(name = "view_id", nullable = false, columnDefinition = "INT")
	private ViewEntity view;

	@Column(name = "bucket", columnDefinition = "VARCHAR(255)")
	private String bucketDescriptor;

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(
			name = "member_buckets",
			joinColumns = @JoinColumn(name = "bucket_id"),
			inverseJoinColumns = @JoinColumn(name = "member_id")
	)
	@OnDelete(action = OnDeleteAction.CASCADE)
	private List<MemberEntity> members;

	public BucketEntity(ViewEntity view, String bucketDescriptor) {
		this.view = view;
		this.bucketDescriptor = bucketDescriptor;
	}
}
