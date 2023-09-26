package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.mongock.changeset12.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import static be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.mongock.changeset12.entities.EventStreamEntityV2.COLLECTION_NAME;

@Document(collection = COLLECTION_NAME)
public class EventStreamEntityV2 {
	public static final String COLLECTION_NAME = "eventstream";

	@Id
	private final String id;
	private final String timestampPath;
	private final String versionOfPath;
	private final String memberType;

	public EventStreamEntityV2(String collectionName, String timestampPath, String versionOfPath, String memberType) {
		this.id = collectionName;
		this.timestampPath = timestampPath;
		this.versionOfPath = versionOfPath;
		this.memberType = memberType;
	}

	public static EventStreamEntityV2 from(EventStreamEntityV1 eventStreamEntityV1) {
		return new EventStreamEntityV2(eventStreamEntityV1.getId(),
				eventStreamEntityV1.getTimestampPath(),
				eventStreamEntityV1.getVersionOfPath(),
				eventStreamEntityV1.getMemberType());
	}

	public String getId() {
		return id;
	}

	public String getTimestampPath() {
		return timestampPath;
	}

	public String getVersionOfPath() {
		return versionOfPath;
	}

	public String getMemberType() {
		return memberType;
	}
}
