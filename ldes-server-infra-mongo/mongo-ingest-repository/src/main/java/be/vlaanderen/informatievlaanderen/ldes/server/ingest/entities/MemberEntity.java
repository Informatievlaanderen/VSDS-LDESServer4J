package be.vlaanderen.informatievlaanderen.ldes.server.ingest.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document("ingest_ldesmember")
@CompoundIndex(name = "collection_seqNr", def = "{'collectionName' : 1, 'sequenceNr': 1}")
public class MemberEntity {

	@Id
	private final String id;
	@Indexed
	private final String collectionName;
	private final String versionOf;
	private final LocalDateTime timestamp;
	private Long sequenceNr;
	private final boolean isInEventSource;
	private final String transactionId;
	private final String model;

	@SuppressWarnings("java:S107")
	public MemberEntity(String id, String collectionName, String versionOf, LocalDateTime timestamp, Long sequenceNr, boolean isInEventSource, String transactionId, String model) {
		this.id = id;
		this.collectionName = collectionName;
		this.versionOf = versionOf;
		this.timestamp = timestamp;
		this.sequenceNr = sequenceNr;
        this.isInEventSource = isInEventSource;
        this.transactionId = transactionId;
		this.model = model;
	}

	public String getId() {
		return id;
	}

	public String getCollectionName() {
		return collectionName;
	}

	public String getVersionOf() {
		return versionOf;
	}

	public LocalDateTime getTimestamp() {
		return timestamp;
	}

	public Long getSequenceNr() {
		return sequenceNr;
	}

	public void setSequenceNr(Long sequenceNr) {
		this.sequenceNr = sequenceNr;
	}

	public String getTransactionId() {
		return transactionId;
	}

	public String getModel() {
		return model;
	}

	public boolean isInEventSource() {
		return isInEventSource;
	}
}
