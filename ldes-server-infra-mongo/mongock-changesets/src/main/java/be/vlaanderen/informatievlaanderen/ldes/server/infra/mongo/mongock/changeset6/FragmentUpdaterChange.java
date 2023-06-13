package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.mongock.changeset6;

import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.mongock.changeset6.entities.LdesFragmentEntityV3;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.mongock.changeset6.entities.LdesFragmentEntityV4;
import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import java.time.LocalDateTime;

@ChangeUnit(id = "fragment-updater-changeset-3", order = "6", author = "VSDS")
public class FragmentUpdaterChange {
	private final MongoTemplate mongoTemplate;

	public FragmentUpdaterChange(MongoTemplate mongoTemplate) {
		this.mongoTemplate = mongoTemplate;
	}

	@Execution
	public void changeSet() {
		mongoTemplate.stream(new Query(), LdesFragmentEntityV3.class).forEach(ldesFragmentEntityV3 -> {
			LdesFragmentEntityV4 ldesFragmentEntityV4 = new LdesFragmentEntityV4(
					ldesFragmentEntityV3.getId(), ldesFragmentEntityV3.getRoot(), ldesFragmentEntityV3.getViewName(),
					ldesFragmentEntityV3.getFragmentPairs(), ldesFragmentEntityV3.getImmutable(),
					ldesFragmentEntityV3.getParentId(), ldesFragmentEntityV3.getNumberOfMembers(),
					ldesFragmentEntityV3.getRelations(), ldesFragmentEntityV3.getCollectionName());
			mongoTemplate.save(ldesFragmentEntityV4);
		});
	}

	@RollbackExecution
	public void rollback() {
		mongoTemplate.stream(new Query(), LdesFragmentEntityV4.class).forEach(ldesFragmentEntityV4 -> {
			boolean defaultSoftDeleted = false;
			LocalDateTime defaultImmutableTimestamp = ldesFragmentEntityV4.getImmutable().equals(Boolean.TRUE)
					? LocalDateTime.now()
					: null;
			LdesFragmentEntityV3 ldesFragmentEntityV3 = new LdesFragmentEntityV3(
					ldesFragmentEntityV4.getId(), ldesFragmentEntityV4.getRoot(), ldesFragmentEntityV4.getViewName(),
					ldesFragmentEntityV4.getFragmentPairs(), ldesFragmentEntityV4.getImmutable(), defaultSoftDeleted,
					ldesFragmentEntityV4.getParentId(), defaultImmutableTimestamp,
					ldesFragmentEntityV4.getNumberOfMembers(),
					ldesFragmentEntityV4.getRelations(), ldesFragmentEntityV4.getCollectionName());
			mongoTemplate.save(ldesFragmentEntityV3);
		});
	}
}