package be.vlaanderen.informatievlaanderen.ldes.server.pagination.postgres.entity;

import jakarta.persistence.*;

import java.io.Serializable;

@Embeddable
public class RelationId implements Serializable {
	@Column(name = "from_page_id", nullable = false, columnDefinition = "BIGINT")
	private Long fromPageId;

	@Column(name = "to_page_id", nullable = false, columnDefinition = "BIGINT")
	private Long toPageId;

	@Override
	public final boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof RelationId that)) return false;

		return fromPageId.equals(that.fromPageId) && toPageId.equals(that.toPageId);
	}

	@Override
	public int hashCode() {
		int result = fromPageId.hashCode();
		result = 31 * result + toPageId.hashCode();
		return result;
	}
}
