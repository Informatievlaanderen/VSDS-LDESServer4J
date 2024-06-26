package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.postgres.entity;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.view.entity.ViewEntity;
import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

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
}
