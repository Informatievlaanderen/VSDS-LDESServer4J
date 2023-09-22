package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.mongock.changeset10.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import static be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.mongock.changeset10.entities.EventStreamEntityV1.COLLECTION_NAME;

@Document(collection = COLLECTION_NAME)
public class EventStreamEntityV1 {

	public static final String COLLECTION_NAME = "eventstreams";

	@Id
	private final String id;
	private final String timestampPath;
	private final String versionOfPath;
	private final String memberType;

	public EventStreamEntityV1(String collectionName, String timestampPath, String versionOfPath, String memberType) {
		this.id = collectionName;
		this.timestampPath = timestampPath;
		this.versionOfPath = versionOfPath;
		this.memberType = memberType;
	}

	public static EventStreamEntityV1 from(EventStreamEntityV2 eventStreamEntityV2) {
		return new EventStreamEntityV1(
				eventStreamEntityV2.getId(),
				eventStreamEntityV2.getTimestampPath(),
				eventStreamEntityV2.getVersionOfPath(),
				eventStreamEntityV2.getMemberType());
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
