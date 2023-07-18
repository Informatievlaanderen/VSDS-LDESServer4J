package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.mongock.changeset4;

import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.mongock.changeset4.entities.LdesMemberEntityV4;
import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

@ChangeUnit(id = "member-updater-changeset-4", order = "4", author = "VSDS")
public class MemberUpdaterChange {

	private final MongoTemplate mongoTemplate;

	public MemberUpdaterChange(MongoTemplate mongoTemplate) {
		this.mongoTemplate = mongoTemplate;
	}

	/**
	 * This is the method with the migration code
	 **/
	@Execution
	public void changeSet() {
		mongoTemplate.stream(new Query(), LdesMemberEntityV4.class).forEach(ldesMember -> {
			final String newId = ldesMember.getCollectionName() + "/" + ldesMember.getId();
			final LdesMemberEntityV4 updatedMember = new LdesMemberEntityV4(newId,
					ldesMember.getCollectionName(), ldesMember.getSequenceNr(),
					ldesMember.getVersionOf(), ldesMember.getTimestamp(), ldesMember.getModel(),
					ldesMember.getTreeNodeReferences());
			// we cannot change the id so we need to insert + delete
			mongoTemplate.save(updatedMember);
			mongoTemplate.remove(ldesMember);
		});
	}

	@RollbackExecution
	public void rollback() {
		mongoTemplate.stream(new Query(), LdesMemberEntityV4.class).forEach(ldesMember -> {
			final String idWithoutPrefix = ldesMember.getId().substring(ldesMember.getId().indexOf("/") + 1);
			final LdesMemberEntityV4 updatedMember = new LdesMemberEntityV4(idWithoutPrefix,
					ldesMember.getCollectionName(), ldesMember.getSequenceNr(),
					ldesMember.getVersionOf(), ldesMember.getTimestamp(), ldesMember.getModel(),
					ldesMember.getTreeNodeReferences());
			// we cannot change the id so we need to insert + delete
			mongoTemplate.save(updatedMember);
			mongoTemplate.remove(ldesMember);
		});
	}

}
