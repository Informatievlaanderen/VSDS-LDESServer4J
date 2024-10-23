package be.vlaanderen.informatievlaanderen.ldes.server.maintenance.postgres.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "page_members")
public class RetentionPageMemberEntity {
	@EmbeddedId
	private PageMemberId pageMemberId;

	@Column(name = "member_id", nullable = false, columnDefinition = "BIGINT", insertable=false, updatable=false)
	private Long memberId;

	@Column(name = "bucket_id", nullable = false, columnDefinition = "BIGINT", insertable=false, updatable=false)
	private Long bucketId;

	@Column(name = "page_id", columnDefinition = "BIGINT")
	private Long pageId;
}
