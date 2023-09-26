package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.mongock.changeset12;

import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.mongock.changeset12.entities.EventStreamEntityV1;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.mongock.changeset12.entities.EventStreamEntityV2;
import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

@ChangeUnit(id = "eventstream-updater-changeset-12", order = "2023-09-22 00:00:00", author = "VSDS")
public class EventStreamUpdaterChange {
	private final MongoTemplate mongoTemplate;

	public EventStreamUpdaterChange(MongoTemplate mongoTemplate) {
		this.mongoTemplate = mongoTemplate;
	}

	@Execution
	public void changeSet() {
		mongoTemplate.stream(new Query(), EventStreamEntityV1.class).forEach(eventStreamEntityV1 -> {
			mongoTemplate.save(EventStreamEntityV2.from(eventStreamEntityV1));
			mongoTemplate.remove(new Query(Criteria.where("_id").is(eventStreamEntityV1.getId())),
					EventStreamEntityV1.class);
		});
		if (mongoTemplate.getCollection(EventStreamEntityV1.COLLECTION_NAME).countDocuments() == 0) {
			mongoTemplate.dropCollection(EventStreamEntityV1.class);
		}
	}

	@RollbackExecution
	public void rollback() {
		mongoTemplate.stream(new Query(), EventStreamEntityV2.class).forEach(eventStreamEntityV2 -> {
			mongoTemplate.save(EventStreamEntityV1.from(eventStreamEntityV2));
			mongoTemplate.remove(new Query(Criteria.where("_id").is(eventStreamEntityV2.getId())),
					EventStreamEntityV2.class);
		});
		if (mongoTemplate.getCollection(EventStreamEntityV2.COLLECTION_NAME).countDocuments() == 0) {
			mongoTemplate.dropCollection(EventStreamEntityV2.class);
		}
	}
}
