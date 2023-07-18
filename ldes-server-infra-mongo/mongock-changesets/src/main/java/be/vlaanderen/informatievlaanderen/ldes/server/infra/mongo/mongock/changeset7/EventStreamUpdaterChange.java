package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.mongock.changeset7;

import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.mongock.changeset7.config.AppConfigChangeset7;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.mongock.changeset7.eventstream.EventStreamEntityV1;
import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;

import static be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.mongock.changeset7.eventstream.EventStreamEntityV1.COLLECTION_NAME;

@ChangeUnit(id = "eventstream-updater-changeset-7", order = "7", author = "VSDS")
public class EventStreamUpdaterChange {

	private static final Logger log = LoggerFactory.getLogger(EventStreamUpdaterChange.class);

	private final MongoTemplate mongoTemplate;
	private final AppConfigChangeset7 config;

	public EventStreamUpdaterChange(MongoTemplate mongoTemplate, AppConfigChangeset7 config) {
		this.mongoTemplate = mongoTemplate;
		this.config = config;
	}

	@Execution
	public void changeSet() {
		if (collectionAlreadyExists()) {
			log.warn("The collection '{}' already exists. Migration for this collection was skipped.", COLLECTION_NAME);
			return;
		}

		config.getCollections().forEach(collection -> {
			mongoTemplate.save(new EventStreamEntityV1(collection.getCollectionName(), collection.getTimestampPath(),
					collection.getVersionOfPath(), collection.getMemberType()));
		});
	}

	private boolean collectionAlreadyExists() {
		return mongoTemplate.getCollection(COLLECTION_NAME).countDocuments() > 0;
	}

	@RollbackExecution
	public void rollback() {
		mongoTemplate.dropCollection(COLLECTION_NAME);
	}

}
