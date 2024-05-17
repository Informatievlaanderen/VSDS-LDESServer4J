package be.vlaanderen.informatievlaanderen.ldes.server.ingest.postgres.entity;


import be.vlaanderen.informatievlaanderen.ldes.server.ingest.postgres.service.MemberEntityListener;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@EntityListeners(MemberEntityListener.class)
@Table(name = "ingest_ldesmember", indexes = {
		@Index(columnList = "collectionName"),
		@Index(columnList = "collectionName, sequenceNr")
})
public class MemberEntity {
	@Id
	private String id;
	private String collectionName;
	private String versionOf;
	private LocalDateTime timestamp;
	private Long sequenceNr;
	private String transactionId;
	private boolean isInEventSource;
	@Column(columnDefinition = "bytea")
	private byte[] model;

	protected MemberEntity() {}

	public MemberEntity(String id, String collectionName, String versionOf, LocalDateTime timestamp, Long sequenceNr,
	                    boolean isInEventSource, String transactionId, byte[] model) {
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

	public boolean isInEventSource() {
		return isInEventSource;
	}

	public void setSequenceNr(Long sequenceNr) {
		this.sequenceNr = sequenceNr;
	}

	public String getTransactionId() {
		return transactionId;
	}

	public byte[] getModel() {
		return model;
	}

}
