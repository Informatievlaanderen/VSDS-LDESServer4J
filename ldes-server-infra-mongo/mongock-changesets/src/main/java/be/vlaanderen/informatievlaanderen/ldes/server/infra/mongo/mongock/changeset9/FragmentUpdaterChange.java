package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.mongock.changeset9;

import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.mongock.changeset9.entities.FragmentEntityV1;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.mongock.changeset9.entities.LdesFragmentEntityV4;
import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

@ChangeUnit(id = "fragment-updater-changeset-9", order = "9", author = "VSDS")
public class FragmentUpdaterChange {
	private final MongoTemplate mongoTemplate;

	public FragmentUpdaterChange(MongoTemplate mongoTemplate) {
		this.mongoTemplate = mongoTemplate;
	}

	/**
	 * This is the method with the migration code
	 **/
	@Execution
	public void changeSet() {
		// noinspection Duplicates
		mongoTemplate.stream(new Query(), LdesFragmentEntityV4.class).forEach(ldesFragmentEntityV4 -> {
			FragmentEntityV1 fragmentEntityV1 = new FragmentEntityV1(ldesFragmentEntityV4.getId(),
					ldesFragmentEntityV4.getRoot(), ldesFragmentEntityV4.getViewName(),
					ldesFragmentEntityV4.getFragmentPairs(), ldesFragmentEntityV4.getImmutable(),
					ldesFragmentEntityV4.getParentId(), ldesFragmentEntityV4.getNumberOfMembers(),
					ldesFragmentEntityV4.getRelations(), ldesFragmentEntityV4.getCollectionName());
			mongoTemplate.save(fragmentEntityV1);
			mongoTemplate.remove(query(where("_id").is(ldesFragmentEntityV4.getId())), LdesFragmentEntityV4.class);
		});
	}

	@RollbackExecution
	public void rollback() {
		// noinspection Duplicates
		mongoTemplate.stream(new Query(), FragmentEntityV1.class).forEach(fragmentEntityV1 -> {
			LdesFragmentEntityV4 ldesFragmentEntityV4 = new LdesFragmentEntityV4(fragmentEntityV1.getId(),
					fragmentEntityV1.getRoot(), fragmentEntityV1.getViewName(),
					fragmentEntityV1.getFragmentPairs(), fragmentEntityV1.getImmutable(),
					fragmentEntityV1.getParentId(), fragmentEntityV1.getNumberOfMembers(),
					fragmentEntityV1.getRelations(), fragmentEntityV1.getCollectionName());
			mongoTemplate.save(ldesFragmentEntityV4);
			mongoTemplate.remove(query(where("_id").is(fragmentEntityV1.getId())), FragmentEntityV1.class);
		});
	}
}