package be.vlaanderen.informatievlaanderen.ldes.server.ingest.membersequence;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "ingest_member_sequence")
public class IngestMemberSequenceEntity {

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
