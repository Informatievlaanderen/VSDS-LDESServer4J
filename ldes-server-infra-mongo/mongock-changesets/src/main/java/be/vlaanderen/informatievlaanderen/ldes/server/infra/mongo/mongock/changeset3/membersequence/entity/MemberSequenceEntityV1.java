package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.mongock.changeset3.membersequence.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "member_sequence")
public class MemberSequenceEntityV1 {

	@Id
	private String id;

	private long seq;

	public MemberSequenceEntityV1() {
		// empty constructor
	}

	public String getId() {
		return id;
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
