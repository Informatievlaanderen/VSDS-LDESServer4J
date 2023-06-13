package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.mongock.changeset3;

import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.mongock.changeset3.config.AppConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.mongock.changeset3.config.LdesConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.mongock.changeset3.entities.LdesMemberEntityV3;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.mongock.changeset3.entities.LdesMemberEntityV4;
import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

@ChangeUnit(id = "member-updater-changeset-3", order = "3", author = "VSDS")
public class MemberUpdaterChange {

	private final MongoTemplate mongoTemplate;
	private final String collection;

	public MemberUpdaterChange(MongoTemplate mongoTemplate, AppConfig appConfig) {
		this.mongoTemplate = mongoTemplate;

		if (appConfig.getCollections().size() == 1) {
			LdesConfig ldesConfig = appConfig.getCollections().get(0);
			collection = ldesConfig.getCollectionName();
		} else {
			collection = "UNDEFINED_COLLECTION";
		}
	}

	/**
	 * This is the method with the migration code
	 **/
	@Execution
	public void changeSet() {
		mongoTemplate.stream(new Query(), LdesMemberEntityV3.class).forEach(ldesMember -> {
			List<String> treeNodeReferences = ldesMember.getTreeNodeReferences().stream()
					.map(treeNode -> "/" + collection + treeNode).toList();
			mongoTemplate
					.save(new LdesMemberEntityV4(ldesMember.getId(), collection, null, ldesMember.getVersionOf(),
							ldesMember.getTimestamp(), ldesMember.getModel(),
							treeNodeReferences));
		});
	}

	@RollbackExecution
	public void rollback() {
		mongoTemplate.stream(new Query(), LdesMemberEntityV4.class).forEach(ldesMember -> {
			List<String> treeNodeReferences = ldesMember.getTreeNodeReferences().stream()
					.map(treeNode -> treeNode.split(collection)[1]).toList();

			mongoTemplate
					.save(new LdesMemberEntityV3(ldesMember.getId(), ldesMember.getVersionOf(),
							ldesMember.getTimestamp(),
							ldesMember.getModel(),
							treeNodeReferences));
		});
	}
}
