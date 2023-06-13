package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.mongock.changeset3.membersequence.service;

import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.mongock.changeset3.membersequence.entity.MemberSequenceEntityV1;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import java.util.Objects;

import static org.springframework.data.mongodb.core.FindAndModifyOptions.options;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

@Component
public class MongockSequenceGeneratorService {

	private final MongoOperations mongoOperations;

	public MongockSequenceGeneratorService(MongoOperations mongoOperations) {
		this.mongoOperations = mongoOperations;
	}

	public long generateSequence(String collectionName) {
		MemberSequenceEntityV1 counter = mongoOperations.findAndModify(
				query(where("_id").is(collectionName)),
				new Update().inc("seq", 1),
				options().returnNew(true).upsert(true),
				MemberSequenceEntityV1.class);
		return !Objects.isNull(counter) ? counter.getSeq() : 1;
	}
}
