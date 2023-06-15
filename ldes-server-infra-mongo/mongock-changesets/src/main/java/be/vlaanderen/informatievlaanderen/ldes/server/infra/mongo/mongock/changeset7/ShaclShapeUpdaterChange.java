package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.mongock.changeset7;

import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.mongock.changeset7.config.AppConfigChangeset7;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.mongock.changeset7.shaclshape.ShaclShapeEntityV1;
import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;
import org.apache.jena.graph.Graph;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.List;

import static be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.mongock.changeset7.shaclshape.ShaclShapeEntityV1.COLLECTION_NAME;

@ChangeUnit(id = "shacl_shape-updater-changeset-7", order = "7", author = "VSDS")
public class ShaclShapeUpdaterChange {

	private static final Logger log = LoggerFactory.getLogger(ShaclShapeUpdaterChange.class);

	private final MongoTemplate mongoTemplate;
	private final AppConfigChangeset7 config;

	public ShaclShapeUpdaterChange(MongoTemplate mongoTemplate, AppConfigChangeset7 config) {
		this.mongoTemplate = mongoTemplate;
		this.config = config;
	}

	@Execution
	public void changeSet() {
		if (collectionAlreadyExists()) {
			log.warn("The collection '{}' already exists. Migration for this collection was skipped.", COLLECTION_NAME);
			return;
		}

		List<ShaclShapeEntityV1> shapes = config.getCollections().stream().map(collection -> {
			String shapePath = collection.validation().getShape();
			final String graphString = determineShape(shapePath);
			return new ShaclShapeEntityV1(collection.getCollectionName(), graphString);
		}).toList();


		mongoTemplate.insertAll(shapes);
	}

	private String determineShape(String shapePath) {
		if (shapePath != null) {
			final Graph graph = RDFDataMgr.loadGraph(shapePath);
			return RDFWriter.source(graph).lang(Lang.TURTLE).asString();
		} else {
			// We use an empty shape when no shape is defined.
			return  "[ a <http://www.w3.org/ns/shacl#NodeShape> ] .";
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
