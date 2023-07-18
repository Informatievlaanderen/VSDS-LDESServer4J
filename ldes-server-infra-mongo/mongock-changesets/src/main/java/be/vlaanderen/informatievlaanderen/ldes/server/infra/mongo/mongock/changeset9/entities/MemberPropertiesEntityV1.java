package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.mongock.changeset9.entities;

import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.mongock.changeset9.valueobjects.LdesFragmentIdentifier;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.mongock.changeset9.valueobjects.ViewName;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Document("retention_member_properties")
public class MemberPropertiesEntityV1 {
	@Indexed
	private final String id;
	@Indexed
	private final String collectionName;
	@Indexed
	private final List<String> views;
	@Indexed
	private final String versionOf;
	@Indexed
	private final LocalDateTime timestamp;

	public MemberPropertiesEntityV1(String id, String collectionName, List<String> views, String versionOf,
			LocalDateTime timestamp) {
		this.id = id;
		this.collectionName = collectionName;
		this.views = views;
		this.versionOf = versionOf;
		this.timestamp = timestamp;
	}

	public static MemberPropertiesEntityV1 from(LdesMemberEntityV4 member) {
		return new MemberPropertiesEntityV1(member.getId(), member.getCollectionName(),
				member.getTreeNodeReferences().stream().map(LdesFragmentIdentifier::fromFragmentId)
						.map(LdesFragmentIdentifier::getViewName).map(ViewName::asString).toList(),
				member.getVersionOf(), member.getTimestamp());
	}

	public String getId() {
		return id;
	}

	public String getCollectionName() {
		return collectionName;
	}

	public String getVersionOf() {
		return versionOf;
	}

}
