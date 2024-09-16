package be.vlaanderen.informatievlaanderen.ldes.server.pagination.postgres.entity;

import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.postgres.entity.BucketEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.postgres.entity.MemberEntity;
import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "page_members")
public class PageMemberEntity {
	@EmbeddedId
	private PageMemberId pageMemberId;

	@MapsId("memberId")
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@OnDelete(action = OnDeleteAction.CASCADE)
	@JoinColumn(name = "member_id", nullable = false, columnDefinition = "BIGINT")
	private MemberEntity member;

	@MapsId("bucketId")
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@OnDelete(action = OnDeleteAction.CASCADE)
	@JoinColumn(name = "bucket_id", nullable = false, columnDefinition = "BIGINT")
	private BucketEntity bucket;

	@ManyToOne(fetch = FetchType.LAZY)
	@OnDelete(action = OnDeleteAction.CASCADE)
	@JoinColumn(name = "page_id", columnDefinition = "BIGINT")
	private PageEntity page;

	public void setPageMemberId(PageMemberId pageMemberId) {
		this.pageMemberId = pageMemberId;
	}

	public void setPage(PageEntity page) {
		this.page = page;
	}

	public void setBucket(BucketEntity bucket) {
		this.bucket = bucket;
	}

	public void setMember(Long memberId) {
		this.member = new MemberEntity(memberId);
	}

	public PageMemberId getPageMemberId() {
		return pageMemberId;
	}

	public MemberEntity getMember() {
		return member;
	}

	public BucketEntity getBucket() {
		return bucket;
	}

	public PageEntity getPage() {
		return page;
	}
}
