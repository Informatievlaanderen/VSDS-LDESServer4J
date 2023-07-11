package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.mongock.changeset10;

import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.mongock.changeset10.entities.IngestMemberEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.mongock.changeset10.entities.LdesMemberEntityV4;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.mongock.changeset10.entities.MemberPropertiesEntityV1;
import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

@ChangeUnit(id = "member-updater-changeset-10", order = "10", author = "VSDS")
public class MemberUpdaterChange {
	private final MongoTemplate mongoTemplate;

	public MemberUpdaterChange(MongoTemplate mongoTemplate) {
		this.mongoTemplate = mongoTemplate;
	}

	@Execution
	public void changeSet() {
		mongoTemplate.stream(new Query(), LdesMemberEntityV4.class).forEach(member -> {
			mongoTemplate.save(MemberPropertiesEntityV1.from(member));
			mongoTemplate.remove(query(where("_id").is(member.getId())), LdesMemberEntityV4.class);
		});
	}

	@RollbackExecution
	public void rollback() {
		mongoTemplate.stream(new Query(), MemberPropertiesEntityV1.class).forEach(memberProperties -> {
			mongoTemplate.save(memberFromMemberProperties(memberProperties));
			mongoTemplate.remove(query(where("_id").is(memberProperties.getId())), MemberPropertiesEntityV1.class);
		});
	}

	private LdesMemberEntityV4 memberFromMemberProperties(MemberPropertiesEntityV1 memberProperties) {
		IngestMemberEntity ingestMember = mongoTemplate.findOne(query(where("_id").is(memberProperties.getId())),
				IngestMemberEntity.class);

		return new LdesMemberEntityV4(memberProperties.getId(), memberProperties.getCollectionName(),
				ingestMember.getSequenceNr(), memberProperties.getVersionOf(), memberProperties.getTimestamp(),
				ingestMember.getModel(), memberProperties.getViews());
	}
}
