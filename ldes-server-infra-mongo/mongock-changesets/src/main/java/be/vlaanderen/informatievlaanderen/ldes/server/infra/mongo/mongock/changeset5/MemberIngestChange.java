package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.mongock.changeset5;

import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.mongock.changeset5.entities.IngestMemberEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.mongock.changeset5.entities.LdesMemberEntityV4;
import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

// Not active yet, preparation for new architecture
//@ChangeUnit(id = "member-ingest-changeset-5", order = "5", author = "VSDS")
public class MemberIngestChange {

	private final MongoTemplate mongoTemplate;

	public MemberIngestChange(MongoTemplate mongoTemplate) {
		this.mongoTemplate = mongoTemplate;
	}

	/**
	 * The new ingest_member collection is seeded from the legacy member collection.
	 * Dropping the legacy collection will happen in a final change-unit when all
	 * server v2 migrations are finished.
	 **/
	@Execution
	public void changeSet() {
		mongoTemplate.stream(new Query(), LdesMemberEntityV4.class).forEach(ldesMember -> {
			mongoTemplate.save(IngestMemberEntity.from(ldesMember));
		});
	}

	@RollbackExecution
	public void rollback() {
		mongoTemplate.dropCollection("ingest_ldesmember");
	}

}
