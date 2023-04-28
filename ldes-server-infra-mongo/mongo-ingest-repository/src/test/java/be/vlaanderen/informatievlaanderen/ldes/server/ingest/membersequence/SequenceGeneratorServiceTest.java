package be.vlaanderen.informatievlaanderen.ldes.server.ingest.membersequence;

import org.junit.jupiter.api.Test;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Update;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

class SequenceGeneratorServiceTest {

	private final MongoOperations mongoOperations = mock(MongoOperations.class);
	private final SequenceGeneratorService sequenceGeneratorService = new SequenceGeneratorService(mongoOperations);

	@Test
	void test_memberSequenceEntityIsNotNull() {
		MemberSequenceEntity memberSequenceEntity = new MemberSequenceEntity();
		memberSequenceEntity.setId("collectionName");
		memberSequenceEntity.setSeq(100);
		when(mongoOperations.findAndModify(eq(query(where("_id").is("collectionName"))),
				eq(new Update().inc("seq", 1)),
				any(),
				eq(MemberSequenceEntity.class))).thenReturn(memberSequenceEntity);
		long sequence = sequenceGeneratorService.generateSequence("collectionName");

		assertEquals(100, sequence);
	}

	@Test
	void test_memberSequenceEntityIsNull() {
		long sequence = sequenceGeneratorService.generateSequence("collectionName");

		assertEquals(1, sequence);
	}

}