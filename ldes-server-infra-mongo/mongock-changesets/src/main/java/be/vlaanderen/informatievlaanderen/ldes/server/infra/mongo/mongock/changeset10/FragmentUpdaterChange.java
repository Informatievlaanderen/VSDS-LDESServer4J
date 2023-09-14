package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.mongock.changeset10;

import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.mongock.changeset10.entities.FragmentEntityV1;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.mongock.changeset10.entities.FragmentEntityV2;
import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

@ChangeUnit(id = "fragment-updater-changeset-10", order = "2023-09-14 00:00:00", author = "VSDS")
public class FragmentUpdaterChange {
	private final MongoTemplate mongoTemplate;

	public FragmentUpdaterChange(MongoTemplate mongoTemplate) {
		this.mongoTemplate = mongoTemplate;
	}

	@Execution
	public void changeSet() {
		mongoTemplate.stream(new Query(), FragmentEntityV1.class).forEach(oldEntity -> {
			FragmentEntityV2 newEntity = new FragmentEntityV2(
					oldEntity.getId(),
					oldEntity.getRoot(),
					oldEntity.getViewName(),
					oldEntity.getFragmentPairs(),
					oldEntity.getImmutable(),
					oldEntity.getParentId(),
					oldEntity.getNumberOfMembers(),
					oldEntity.getRelations(),
					oldEntity.getCollectionName(),
					oldEntity.getDeleteTime());
			mongoTemplate.save(newEntity);
		});
	}

	@RollbackExecution
	public void rollback() {
		mongoTemplate.stream(new Query(), FragmentEntityV2.class).forEach(v2Entity -> {
			FragmentEntityV1 v1Entity = new FragmentEntityV1(
					v2Entity.getId(),
					v2Entity.getRoot(),
					v2Entity.getViewName(),
					v2Entity.getFragmentPairs(),
					v2Entity.getImmutable(),
					v2Entity.getParentId(),
					v2Entity.getNrOfMembersAdded(),
					v2Entity.getRelations(),
					v2Entity.getCollectionName(),
					v2Entity.getDeleteTime());
			mongoTemplate.save(v1Entity);
		});
	}
}