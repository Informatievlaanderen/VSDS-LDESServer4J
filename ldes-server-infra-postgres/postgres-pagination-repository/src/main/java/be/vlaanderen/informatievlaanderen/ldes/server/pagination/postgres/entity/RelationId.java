package be.vlaanderen.informatievlaanderen.ldes.server.pagination.postgres.entity;

import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;

import java.io.Serializable;

@Embeddable
public class RelationId implements Serializable {
	@ManyToOne
	@JoinColumn(name = "from_page_id", nullable = false)
	private PageEntity fromPage;

	@ManyToOne
	@JoinColumn(name = "to_page_id", nullable = false)
	private PageEntity toPage;

	public RelationId() {}

	public RelationId(PageEntity fromPage, PageEntity toPage) {
		this.fromPage = fromPage;
		this.toPage = toPage;
	}

	public PageEntity getFromPage() {
		return fromPage;
	}

	public PageEntity getToPage() {
		return toPage;
	}

	@Override
	public final boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof RelationId that)) return false;

		return fromPage.equals(that.fromPage) && toPage.equals(that.toPage);
	}

	@Override
	public int hashCode() {
		int result = fromPage.hashCode();
		result = 31 * result + toPage.hashCode();
		return result;
	}
}
