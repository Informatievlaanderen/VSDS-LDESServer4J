package be.vlaanderen.informatievlaanderen.ldes.server.maintenance.postgres.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "page_members")
public class RetentionPageMemberEntity {
	@EmbeddedId
	private RetentionPageMemberId pageMemberId;

	@Column(name = "member_id", nullable = false, columnDefinition = "BIGINT", insertable=false, updatable=false)
	private Long memberId;

	@Column(name = "bucket_id", nullable = false, columnDefinition = "BIGINT", insertable=false, updatable=false)
	private Long bucketId;

	@Column(name = "page_id", columnDefinition = "BIGINT")
	private Long pageId;

	public void setPageId(Long pageId) {
		this.pageId = pageId;
	}

	public void setMemberId(Long memberId) {
		this.pageMemberId.setMemberId(memberId);
	}

	public Long getMemberId() {
		return this.pageMemberId.getMemberId();
	}

	public Long getBucketId() {
		return this.pageMemberId.getBucketId();
	}

	public Long getPageId() {
		return pageId;
	}
}
