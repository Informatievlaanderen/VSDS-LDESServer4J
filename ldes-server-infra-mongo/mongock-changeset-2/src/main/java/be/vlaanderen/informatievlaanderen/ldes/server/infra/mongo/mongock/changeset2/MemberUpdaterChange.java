package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.mongock.changeset2;

import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.mongock.changeset1.entities.LdesMemberEntityV2;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.mongock.changeset2.entities.LdesMemberEntityV3;
import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.time.LocalDateTime;
import java.util.List;

@ChangeUnit(id = "member-updater-changeset-2", order = "2", author = "VSDS")
public class MemberUpdaterChange {

	private static final String MEMBERS = "members";
	private final MongoTemplate mongoTemplate;

	public MemberUpdaterChange(MongoTemplate mongoTemplate) {
		this.mongoTemplate = mongoTemplate;
	}

	/**
	 * This is the method with the migration code
	 **/
	@Execution
	public void changeSet() {
		List<LdesMemberEntityV2> ldesMemberEntityV2s = mongoTemplate.find(new Query(), LdesMemberEntityV2.class);
		ldesMemberEntityV2s.forEach(ldesMember -> {
			List<String> treeNodeReferences = getTreeNodeReferences(ldesMember);

			String versionOf = "";
			LocalDateTime timestamp = LocalDateTime.now();

			mongoTemplate
					.save(new LdesMemberEntityV3(ldesMember.getId(), ldesMember.getModel(), versionOf, timestamp,
							treeNodeReferences));
		});
	}

	@RollbackExecution
	public void rollback() {
		List<LdesMemberEntityV3> ldesMemberEntities = mongoTemplate.find(new Query(), LdesMemberEntityV3.class);

		ldesMemberEntities.forEach(ldesMember -> {
			List<String> treeNodeReferences = getTreeNodeReferences(ldesMember);

			mongoTemplate
					.save(new LdesMemberEntityV2(ldesMember.getId(), ldesMember.getModel(), treeNodeReferences));
		});
	}

	private List<String> getTreeNodeReferences(LdesMemberEntityV2 ldesMember) {
		Query query = new Query();
		query.addCriteria(Criteria.where(MEMBERS).is(ldesMember.getId()));

		return mongoTemplate.find(query, LdesMemberEntityV2.class).stream()
				.map(LdesMemberEntityV2::getId).toList();
	}
}
