package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.mongock.changeset10.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("ingest_ldesmember")
public class IngestMemberEntity {

	@Id
	private final String id;

	@Indexed
	private final String collectionName;

	@Indexed
	private Long sequenceNr;

	private final String model;

	public IngestMemberEntity(String id, String collectionName, Long sequenceNr, String model) {
		this.id = id;
		this.collectionName = collectionName;
		this.sequenceNr = sequenceNr;
		this.model = model;
	}

	public String getId() {
		return id;
	}

	public String getCollectionName() {
		return collectionName;
	}

	public Long getSequenceNr() {
		return sequenceNr;
	}

	public void setSequenceNr(Long sequenceNr) {
		this.sequenceNr = sequenceNr;
	}

	public String getModel() {
		return model;
	}

}
