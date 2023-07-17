package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.mongock.changeset9;

import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.mongock.changeset9.entities.AllocationEntityV1;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.mongock.changeset9.entities.AllocationEntityV1.AllocationKey;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.mongock.changeset9.entities.LdesMemberEntityV4;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.mongock.changeset9.entities.valueobjects.LdesFragmentIdentifier;
import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

@ChangeUnit(id = "allocation-updater-changeset-9", order = "2023-07-14 00:00:00", author = "VSDS")
public class AllocationUpdaterChange {
	private final MongoTemplate mongoTemplate;

	public AllocationUpdaterChange(MongoTemplate mongoTemplate) {
		this.mongoTemplate = mongoTemplate;
	}

	/**
	 * This is the method with the migration code
	 **/
	@Execution
	public void changeSet() {
		// noinspection Duplicates
		mongoTemplate.stream(new Query(), LdesMemberEntityV4.class).forEach(ldesMemberEntityV4 -> {

			ldesMemberEntityV4.getTreeNodeReferences().forEach(fragmentId -> {
				AllocationEntityV1 allocationEntityV1 = new AllocationEntityV1(
						new AllocationKey(ldesMemberEntityV4.getId(), fragmentId),
						LdesFragmentIdentifier.fromFragmentId(fragmentId).getViewName());

				mongoTemplate.save(allocationEntityV1);
			});
		});
	}

	@RollbackExecution
	public void rollback() {
		// noinspection Duplicates
		mongoTemplate.stream(new Query(), AllocationEntityV1.class).forEach(fragmentEntityV1 -> {
			mongoTemplate.remove(query(where("_id").is(fragmentEntityV1.getAllocationKey())), AllocationEntityV1.class);
		});
	}
}
