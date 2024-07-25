package be.vlaanderen.informatievlaanderen.ldes.server.pagination.postgres.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;

@Embeddable
public class PageMemberId implements Serializable {
	@Column(name = "member_id", nullable = false, columnDefinition = "BIGINT")
	private Long memberId;

	@Column(name = "bucket_id", nullable = false, columnDefinition = "BIGINT")
	private Long bucketId;

	@Column(name = "page_id", nullable = false, columnDefinition = "BIGINT")
	private Long pageId;

	@Override
	public final boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof PageMemberId that)) return false;

		return memberId.equals(that.memberId) && bucketId.equals(that.bucketId) && pageId.equals(that.pageId);
	}

	@Override
	public int hashCode() {
		int result = memberId.hashCode();
		result = 31 * result + bucketId.hashCode();
		result = 31 * result + pageId.hashCode();
		return result;
	}
}
