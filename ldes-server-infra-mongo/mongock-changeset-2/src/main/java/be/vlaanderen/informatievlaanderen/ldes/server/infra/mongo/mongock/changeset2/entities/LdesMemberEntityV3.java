package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.mongock.changeset2.entities;

import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.mongock.changeset1.entities.LdesMemberEntityV2;

import java.time.LocalDateTime;
import java.util.List;

public class LdesMemberEntityV3 extends LdesMemberEntityV2 {

	private final String versionOf;
	private final LocalDateTime timestamp;

	public LdesMemberEntityV3(String id, final String model, final String versionOf, final LocalDateTime timestamp,
			List<String> treeNodeReferences) {
		super(id, model, treeNodeReferences);

		this.versionOf = versionOf;
		this.timestamp = timestamp;
	}

	public String getVersionOf() {
		return versionOf;
	}

	public LocalDateTime getTimestamp() {
		return timestamp;
	}
}
