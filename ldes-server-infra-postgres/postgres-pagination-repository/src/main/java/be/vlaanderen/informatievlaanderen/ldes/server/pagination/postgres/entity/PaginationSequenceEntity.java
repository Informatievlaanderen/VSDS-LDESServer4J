package be.vlaanderen.informatievlaanderen.ldes.server.pagination.postgres.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "pagination_sequence")
public class PaginationSequenceEntity {

	@Id
	private String viewName;

	private long lastProcessedSequence;

	protected PaginationSequenceEntity() {}

	public PaginationSequenceEntity(String viewName, long lastProcessedSequence) {
		this.viewName = viewName;
		this.lastProcessedSequence = lastProcessedSequence;
	}

	public String getViewName() {
		return viewName;
	}

	public long getLastProcessedSequence() {
		return lastProcessedSequence;
	}
}
