package be.vlaanderen.informatievlaanderen.ldes.server.pagination.postgres.entity;

import jakarta.persistence.*;

import java.io.Serializable;

@Embeddable
public class RelationId implements Serializable {
	@Column(name = "from_page_id", nullable = false, columnDefinition = "BIGINT")
	private Long fromPageId;

	@Column(name = "to_page_id", nullable = false, columnDefinition = "BIGINT")
	private Long toPageId;

}
