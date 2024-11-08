package be.vlaanderen.informatievlaanderen.ldes.server.fetch.postgres.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;

@Embeddable
public class RelationId implements Serializable {
	@Column(name = "from_page_id", nullable = false, columnDefinition = "BIGINT")
	private Long fromPageId;

	@Column(name = "to_page_id", nullable = false, columnDefinition = "BIGINT")
	private Long toPageId;

	public RelationId() {
	}

	public RelationId(Long fromPageId, Long toPageId) {
		this.fromPageId = fromPageId;
		this.toPageId = toPageId;
	}

	public Long getFromPageId() {
		return fromPageId;
	}

	public Long getToPageId() {
		return toPageId;
	}

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
