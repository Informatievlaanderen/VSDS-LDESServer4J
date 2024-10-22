package be.vlaanderen.informatievlaanderen.ldes.server.fetch.postgres.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "page_members")
public class FetchPageMemberEntity {
	@EmbeddedId
	private PageMemberId pageMemberId;

	@MapsId("memberId")
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@OnDelete(action = OnDeleteAction.CASCADE)
	@JoinColumn(name = "member_id", nullable = false, columnDefinition = "BIGINT")
	private FetchMemberEntity member;

	@Column(name = "page_id", columnDefinition = "BIGINT")
	private Long pageId;

	public void setPageId(Long pageId) {
		this.pageId = pageId;
	}

	public void setMember(Long memberId) {
		this.member = new FetchMemberEntity(memberId);
	}

	public FetchMemberEntity getMember() {
		return member;
	}

	public Long getPageId() {
		return pageId;
	}
}
