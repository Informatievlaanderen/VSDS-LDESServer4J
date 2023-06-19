package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.mongock.changeset3;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewSpecification;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.mongock.changeset3.config.AppConfigChangeset3;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.mongock.changeset3.config.LdesConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.mongock.changeset3.entities.LdesFragmentEntityV2;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.mongock.changeset3.entities.LdesFragmentEntityV3;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.mongock.changeset3.valueobjects.TreeRelation;
import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

@ChangeUnit(id = "fragment-updater-changeset-2", order = "3", author = "VSDS")
public class FragmentUpdaterChange {
	private final MongoTemplate mongoTemplate;
	private final String collection;
	private final List<String> viewNames;

	public FragmentUpdaterChange(MongoTemplate mongoTemplate, AppConfigChangeset3 appConfigChangeset3) {
		this.mongoTemplate = mongoTemplate;

		if (appConfigChangeset3.getCollections().size() == 1) {
			LdesConfig ldesConfig = appConfigChangeset3.getCollections().get(0);
			collection = ldesConfig.getCollectionName();
			viewNames = ldesConfig.getViews().stream().map(ViewSpecification::getName).map(s -> "/" + s.asString())
					.toList();
		} else {
			collection = "UNDEFINED_COLLECTION";
			viewNames = List.of("UNDEFINED_VIEWNAME");
		}
	}

	/**
	 * This is the method with the migration code
	 **/
	@Execution
	public void changeSet() {
		mongoTemplate.stream(new Query(), LdesFragmentEntityV2.class).forEach(ldesFragmentEntityV2 -> {
			if (!viewNames.contains(ldesFragmentEntityV2.getId())) {
				List<TreeRelation> updatedRelations = ldesFragmentEntityV2.getRelations().stream()
						.map(treeRelation -> new TreeRelation(treeRelation.treePath(),
								addCollectionPrefix(treeRelation.treeNode()), treeRelation.treeValue(),
								treeRelation.treeValueType(), treeRelation.relation()))
						.toList();
				LdesFragmentEntityV3 ldesFragmentEntity = new LdesFragmentEntityV3(
						addCollectionPrefix(ldesFragmentEntityV2.getId()),
						ldesFragmentEntityV2.getRoot(), collection + "/" + ldesFragmentEntityV2.getViewName(),
						ldesFragmentEntityV2.getFragmentPairs(),
						ldesFragmentEntityV2.getImmutable(), ldesFragmentEntityV2.getSoftDeleted(),
						ldesFragmentEntityV2.getParentId().equals("root") ? ldesFragmentEntityV2.getParentId()
								: addCollectionPrefix(ldesFragmentEntityV2.getParentId()),
						ldesFragmentEntityV2.getImmutableTimestamp(),
						ldesFragmentEntityV2.getNumberOfMembers(),
						updatedRelations, collection);
				mongoTemplate.save(ldesFragmentEntity);
				mongoTemplate.remove(query(where("_id").is(ldesFragmentEntityV2.getId())), LdesFragmentEntityV2.class);
			}
		});
	}

	private String addCollectionPrefix(String ldesFragmentEntityV2) {
		return "/" + collection + ldesFragmentEntityV2;
	}

	@RollbackExecution
	public void rollback() {
		mongoTemplate.stream(new Query(), LdesFragmentEntityV3.class).forEach(fragmentEntity -> {
			List<TreeRelation> updatedRelations = fragmentEntity.getRelations().stream()
					.map(treeRelation -> new TreeRelation(treeRelation.treePath(),
							removeCollectionPrefix(treeRelation.treeNode()), treeRelation.treeValue(),
							treeRelation.treeValueType(), treeRelation.relation()))
					.toList();
			LdesFragmentEntityV2 ldesFragmentEntityV2 = new LdesFragmentEntityV2(
					removeCollectionPrefix(fragmentEntity.getId()),
					fragmentEntity.getRoot(), fragmentEntity.getViewName(),
					fragmentEntity.getFragmentPairs(),
					fragmentEntity.getImmutable(), fragmentEntity.getSoftDeleted(),
					fragmentEntity.getParentId().equals("root") ? fragmentEntity.getParentId()
							: removeCollectionPrefix(fragmentEntity.getParentId()),
					fragmentEntity.getImmutableTimestamp(), fragmentEntity.getNumberOfMembers(),
					updatedRelations);
			mongoTemplate.save(ldesFragmentEntityV2);
			mongoTemplate.remove(query(where("_id").is(fragmentEntity.getId())), LdesFragmentEntityV3.class);

		});
	}

	private String removeCollectionPrefix(String s) {
		return s.split(collection)[1];
	}
}