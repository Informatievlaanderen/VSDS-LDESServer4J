package be.vlaanderen.informatievlaanderen.ldes.server.ingest.membersequence;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "member_sequence")
public class MemberSequenceEntity {

	@Id
	private String id;

	private long seq;

	public MemberSequenceEntity() {
		// empty constructor
	}

	public long getSeq() {
		return seq;
	}

}
