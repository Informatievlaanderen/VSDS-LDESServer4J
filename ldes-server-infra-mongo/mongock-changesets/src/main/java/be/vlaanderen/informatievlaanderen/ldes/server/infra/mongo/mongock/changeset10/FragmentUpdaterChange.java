package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.mongock.changeset10;

import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.mongock.changeset10.entities.FragmentEntityV1;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.mongock.changeset10.entities.FragmentEntityV2;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.mongock.changeset10.entities.valueobjects.LdesFragmentIdentifier;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.mongock.changeset10.entities.valueobjects.TreeRelationV1;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.mongock.changeset10.entities.valueobjects.TreeRelationV2;
import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

@ChangeUnit(id = "fragment-updater-changeset-10", order = "2023-09-10 00:00:00", author = "VSDS")
public class FragmentUpdaterChange {
	private final MongoTemplate mongoTemplate;

	public FragmentUpdaterChange(MongoTemplate mongoTemplate) {
		this.mongoTemplate = mongoTemplate;
	}

	@Execution
	public void changeSet() {
		mongoTemplate.stream(new Query(), FragmentEntityV1.class).forEach(oldEntity -> {
			List<TreeRelationV2> relations = oldEntity.getRelations().stream().map(treeRelationV1 -> {
				var ldesFragmentIdentifier = LdesFragmentIdentifier.fromFragmentId(treeRelationV1.treeNode());
				return new TreeRelationV2(treeRelationV1.treePath(), ldesFragmentIdentifier,
						treeRelationV1.treeValue(), treeRelationV1.treeValueType(), treeRelationV1.relation());
			}).toList();

			FragmentEntityV2 newEntity = new FragmentEntityV2(
					oldEntity.getId(),
					oldEntity.getRoot(),
					oldEntity.getViewName(),
					oldEntity.getFragmentPairs(),
					oldEntity.getImmutable(),
					oldEntity.getParentId(),
					oldEntity.getNumberOfMembers(),
					relations,
					oldEntity.getCollectionName(),
					oldEntity.getDeleteTime());
			mongoTemplate.save(newEntity);
		});
	}

	@RollbackExecution
	public void rollback() {
		mongoTemplate.stream(new Query(), FragmentEntityV2.class).forEach(newEntity -> {
			List<TreeRelationV1> relations = newEntity.getRelations().stream().map(treeRelationV2 -> {
				var treeNode = treeRelationV2.treeNode().asString();
				return new TreeRelationV1(treeRelationV2.treePath(), treeNode,
						treeRelationV2.treeValue(), treeRelationV2.treeValueType(), treeRelationV2.relation());
			}).toList();

			FragmentEntityV1 oldEntity = new FragmentEntityV1(
					newEntity.getId(),
					newEntity.getRoot(),
					newEntity.getViewName(),
					newEntity.getFragmentPairs(),
					newEntity.getImmutable(),
					newEntity.getParentId(),
					newEntity.getNrOfMembersAdded(),
					relations,
					newEntity.getCollectionName(),
					newEntity.getDeleteTime());
			mongoTemplate.save(oldEntity);
		});
	}
}