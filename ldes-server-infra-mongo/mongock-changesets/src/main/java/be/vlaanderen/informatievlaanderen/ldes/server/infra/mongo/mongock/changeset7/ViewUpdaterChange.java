package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.mongock.changeset7;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.RdfModelConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.FragmentationConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewSpecification;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.mongock.changeset7.config.AppConfigChangeset7;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.mongock.changeset7.config.LdesConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.mongock.changeset7.entities.ViewEntityV1;
import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;
import org.apache.jena.riot.Lang;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.mongock.changeset7.entities.ViewEntityV1.COLLECTION_NAME;

@ChangeUnit(id = "view-updater-changeset-7", order = "7", author = "VSDS")
public class ViewUpdaterChange {

	private static final Map<String, String> newFragmentationNameMap = Map.of(
			"pagination", "PaginationFragmentation",
			"geospatial", "GeospatialFragmentation",
			"substring", "SubstringFragmentation",
			"timebased", "TimebasedFragmentation"
	);

	private static final Logger log = LoggerFactory.getLogger(ViewUpdaterChange.class);

	private final MongoTemplate mongoTemplate;
	private final AppConfigChangeset7 config;

	public ViewUpdaterChange(MongoTemplate mongoTemplate, AppConfigChangeset7 config) {
		this.mongoTemplate = mongoTemplate;
		this.config = config;
	}
	// TODO: 14/06/23 readme

	@Execution
	public void changeSet() {
		if (collectionAlreadyExists()) {
			log.warn("The collection '{}' already exists. Migration for this collection was skipped.", COLLECTION_NAME);
			return;
		}

		List<ViewEntityV1> views = config.getCollections()
				.stream()
				.map(LdesConfig::getViews)
				.flatMap(Collection::stream)
				.map(this::mapToEntity)
				.toList();

		mongoTemplate.insertAll(views);
	}

	private ViewEntityV1 mapToEntity(ViewSpecification viewSpecification) {
		List<String> serializedRetentionModels = viewSpecification
				.getRetentionConfigs()
				.stream()
				.map(retentionModel -> RdfModelConverter.toString(retentionModel, Lang.NQUADS))
				.toList();
		return new ViewEntityV1(viewSpecification.getName().asString(), serializedRetentionModels,
				viewSpecification.getFragmentations().stream().map(this::renameFragmentations).toList());
	}

	private FragmentationConfig renameFragmentations(FragmentationConfig fragmentationConfig) {
		String fragmentationName = fragmentationConfig.getName();
		fragmentationConfig.setName(newFragmentationNameMap.getOrDefault(fragmentationName, fragmentationName));
		return fragmentationConfig;
	}

	private boolean collectionAlreadyExists() {
		return mongoTemplate.getCollection(COLLECTION_NAME).countDocuments() > 0;
	}

	@RollbackExecution
	public void rollback() {
		mongoTemplate.dropCollection(COLLECTION_NAME);
	}

}
