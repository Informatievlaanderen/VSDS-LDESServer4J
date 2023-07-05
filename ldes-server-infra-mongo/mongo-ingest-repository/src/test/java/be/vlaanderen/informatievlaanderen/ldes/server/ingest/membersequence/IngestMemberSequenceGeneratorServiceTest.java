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

class IngestMemberSequenceGeneratorServiceTest {

	private final MongoOperations mongoOperations = mock(MongoOperations.class);
	private final IngestMemberSequenceGeneratorService ingestMemberSequenceGeneratorService = new IngestMemberSequenceGeneratorService(
			mongoOperations);

	@Test
	void test_memberSequenceEntityIsNotNull() {
		IngestMemberSequenceEntity ingestMemberSequenceEntity = new IngestMemberSequenceEntity();
		ingestMemberSequenceEntity.setId("collectionName");
		ingestMemberSequenceEntity.setSeq(100);
		when(mongoOperations.findAndModify(eq(query(where("_id").is("collectionName"))),
				eq(new Update().inc("seq", 1)),
				any(),
				eq(IngestMemberSequenceEntity.class))).thenReturn(ingestMemberSequenceEntity);
		long sequence = ingestMemberSequenceGeneratorService.generateSequence("collectionName");

		assertEquals(100, sequence);
	}

	@Test
	void test_memberSequenceEntityIsNull() {
		long sequence = ingestMemberSequenceGeneratorService.generateSequence("collectionName");

		assertEquals(1, sequence);
	}

}