package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "fragmentation_sequence")
public class SequenceEntity {

	@Id
	private String viewName;

	private long lastProcessedSequence;

	protected SequenceEntity() {}

	public SequenceEntity(String viewName, long lastProcessedSequence) {
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
