package be.vlaanderen.informatievlaanderen.ldes.server.ingest.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("ingest_ldesmember")
@CompoundIndex(name = "collection_seqNr", def = "{'collectionName' : 1, 'sequenceNr': 1}")
public class MemberEntity {

	@Id
	private final String id;

	@Indexed
	private final String collectionName;

	private Long sequenceNr;

	private final String model;

	public MemberEntity(String id, String collectionName, Long sequenceNr, String model) {
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
