package be.vlaanderen.informatievlaanderen.ldes.server.ingest.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = IngestMemberSequenceEntity.COLLECTION_NAME)
public class IngestMemberSequenceEntity {

	public static final String COLLECTION_NAME = "ingest_member_sequence";

	@Id
	private String id;

	private long seq;

	public IngestMemberSequenceEntity() {
		// empty constructor
	}

	public void setId(String id) {
		this.id = id;
	}

	public long getSeq() {
		return seq;
	}

	public void setSeq(long seq) {
		this.seq = seq;
	}

}
