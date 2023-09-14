package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.mongock.changeset11;

import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.mongock.changeset11.entities.FragmentEntityV2;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.mongock.changeset11.entities.FragmentEntityV3;
import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

@ChangeUnit(id = "fragment-updater-changeset-11", order = "2023-09-14 00:00:00", author = "VSDS")
public class FragmentUpdaterChange {
	private final MongoTemplate mongoTemplate;

	public FragmentUpdaterChange(MongoTemplate mongoTemplate) {
		this.mongoTemplate = mongoTemplate;
	}

	@Execution
	public void changeSet() {
		mongoTemplate.stream(new Query(), FragmentEntityV2.class).forEach(oldEntity -> {
			FragmentEntityV3 newEntity = new FragmentEntityV3(
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
		mongoTemplate.stream(new Query(), FragmentEntityV3.class).forEach(newEntity -> {
			FragmentEntityV2 oldEntity = new FragmentEntityV2(
					newEntity.getId(),
					newEntity.getRoot(),
					newEntity.getViewName(),
					newEntity.getFragmentPairs(),
					newEntity.getImmutable(),
					newEntity.getParentId(),
					newEntity.getNrOfMembersAdded(),
					newEntity.getRelations(),
					newEntity.getCollectionName(),
					newEntity.getDeleteTime());
			mongoTemplate.save(oldEntity);
		});
	}
}