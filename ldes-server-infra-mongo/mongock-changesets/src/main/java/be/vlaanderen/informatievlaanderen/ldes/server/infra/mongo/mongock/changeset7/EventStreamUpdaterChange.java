package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.mongock.changeset7;

import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.mongock.changeset7.config.AppConfigChangeset7;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.mongock.changeset7.entities.EventStreamEntityV1;
import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;

@ChangeUnit(id = "eventstream-updater-changeset-7", order = "7", author = "VSDS")
public class EventStreamUpdaterChange {

	private static final Logger log = LoggerFactory.getLogger(EventStreamUpdaterChange.class);

	private final MongoTemplate mongoTemplate;
	private final AppConfigChangeset7 config;

	public EventStreamUpdaterChange(MongoTemplate mongoTemplate, AppConfigChangeset7 config) {
		this.mongoTemplate = mongoTemplate;
		this.config = config;
	}
	// TODO: 14/06/23 mapping for Jan

	@Execution
	public void changeSet() {
		if (collectionAlreadyExists()) {
			log.warn("The collection '{}' already exists. Migration for this collection was skipped.", EventStreamEntityV1.COLLECTION_NAME);
			return;
		}

		config.getCollections().forEach(collection -> {
			mongoTemplate.save(new EventStreamEntityV1(collection.getCollectionName(), collection.getTimestampPath(),
					collection.getVersionOfPath(), collection.getMemberType()));
		});
	}

	private boolean collectionAlreadyExists() {
		long count = mongoTemplate.getCollection(EventStreamEntityV1.COLLECTION_NAME).countDocuments();
		return count > 0;
	}

	@RollbackExecution
	public void rollback() {
		mongoTemplate.dropCollection("eventstreams");
	}

}
