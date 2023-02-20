package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.mongock.migrationpackage1;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("ldesmember")
public class LdesMemberEntityV1 {

	@Id
	private final String id;

	private final String ldesMember;

	public LdesMemberEntityV1(String id, final String ldesMember) {
		this.id = id;
		this.ldesMember = ldesMember;
	}

	public String getId() {
		return id;
	}

	public String getLdesMember() {
		return this.ldesMember;
	}

}
