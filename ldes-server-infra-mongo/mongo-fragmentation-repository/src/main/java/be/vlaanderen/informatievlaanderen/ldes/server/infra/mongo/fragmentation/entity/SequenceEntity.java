package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.fragmentation.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("fragmentation_sequence")
public class SequenceEntity {

	@Id
	private final String viewName;

	private final long lastProcessedSequence;

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
