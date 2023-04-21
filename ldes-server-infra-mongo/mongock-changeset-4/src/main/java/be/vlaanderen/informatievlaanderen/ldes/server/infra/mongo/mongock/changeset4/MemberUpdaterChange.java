package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.mongock.changeset4;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.AppConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.LdesConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.mongock.changeset4.entities.LdesMemberEntityV4;
import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.impl.LiteralImpl;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParserBuilder;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import java.time.LocalDateTime;
import java.util.List;

import static org.apache.jena.rdf.model.ResourceFactory.createProperty;

@ChangeUnit(id = "member-updater-changeset-2", order = "2", author = "VSDS")
public class MemberUpdaterChange {

	private final MongoTemplate mongoTemplate;

	public MemberUpdaterChange(MongoTemplate mongoTemplate, AppConfig appConfig) {
		this.mongoTemplate = mongoTemplate;
	}

	/**
	 * This is the method with the migration code
	 **/
	@Execution
	public void changeSet() {
		List<LdesMemberEntityV4> ldesMemberEntities = mongoTemplate.find(new Query(), LdesMemberEntityV4.class);
		ldesMemberEntities.forEach(ldesMember -> {
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
		List<LdesMemberEntityV4> ldesMemberEntities = mongoTemplate.find(new Query(), LdesMemberEntityV4.class);

		ldesMemberEntities.forEach(ldesMember -> mongoTemplate
				.save(new LdesMemberEntityV4(ldesMember.getId(), ldesMember.getModel(),
						ldesMember.getTreeNodeReferences())));
	}

}
