package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.mongock.changeset7;

import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.mongock.changeset7.config.AppConfigChangeset7;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.mongock.changeset7.config.LdesConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.mongock.changeset7.view.ViewEntityV1;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.mongock.changeset7.view.ViewEntityV1Mapper;
import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.Collection;
import java.util.List;

import static be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.mongock.changeset7.view.ViewEntityV1.COLLECTION_NAME;

@ChangeUnit(id = "view-updater-changeset-7", order = "2023-06-15 00:00:00", author = "VSDS")
public class ViewUpdaterChange {

	private static final Logger log = LoggerFactory.getLogger(ViewUpdaterChange.class);

	private final MongoTemplate mongoTemplate;
	private final AppConfigChangeset7 config;

	public ViewUpdaterChange(MongoTemplate mongoTemplate, AppConfigChangeset7 config) {
		this.mongoTemplate = mongoTemplate;
		this.config = config;
	}

	@Execution
	public void changeSet() {
		if (collectionAlreadyExists()) {
			log.warn("The collection '{}' already exists. Migration for this collection was skipped.", COLLECTION_NAME);
			return;
		}
		if (config.getCollections() != null) {
			List<ViewEntityV1> views = config.getCollections()
					.stream()
					.map(LdesConfig::getViews)
					.flatMap(Collection::stream)
					.map(ViewEntityV1Mapper::mapToEntity)
					.toList();

			mongoTemplate.insertAll(views);
		}
	}

	private boolean collectionAlreadyExists() {
		return mongoTemplate.getCollection(COLLECTION_NAME).countDocuments() > 0;
	}

	@RollbackExecution
	public void rollback() {
		mongoTemplate.dropCollection(COLLECTION_NAME);
	}

}
