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
}
