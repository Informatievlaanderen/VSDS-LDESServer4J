package be.vlaanderen.informatievlaanderen.ldes.server.ingest.membersequence;

import be.vlaanderen.informatievlaanderen.ldes.server.ingest.entities.IngestMemberSequenceEntity;
import org.junit.jupiter.api.Test;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Update;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

class IngestMemberSequenceServiceTest {

	private final MongoOperations mongoOperations = mock(MongoOperations.class);
	private final IngestMemberSequenceService ingestMemberSequenceService = new IngestMemberSequenceService(
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
		long sequence = ingestMemberSequenceService.generateSequence("collectionName");

		assertEquals(100, sequence);
	}

	@Test
	void test_memberSequenceEntityIsNull() {
		long sequence = ingestMemberSequenceService.generateSequence("collectionName");

		assertEquals(1, sequence);
	}

	@Test
	void when_MultipleLDESes_Then_getCorrectSequence() {
		IngestMemberSequenceEntity ingestMemberSequenceEntity = new IngestMemberSequenceEntity();
		ingestMemberSequenceEntity.setId("collectionName");
		ingestMemberSequenceEntity.setSeq(100);
		when(mongoOperations.find(eq(query(where("_id").is("collectionName"))),
				eq(IngestMemberSequenceEntity.class))).thenReturn(List.of(ingestMemberSequenceEntity));
		IngestMemberSequenceEntity ingestMemberSequenceEntity2 = new IngestMemberSequenceEntity();
		ingestMemberSequenceEntity2.setId("otherCollectionName");
		ingestMemberSequenceEntity2.setSeq(150);
		when(mongoOperations.find(eq(query(where("_id").is("otherCollectionName"))),
				eq(IngestMemberSequenceEntity.class))).thenReturn(List.of(ingestMemberSequenceEntity2));
		when(mongoOperations.findAll(IngestMemberSequenceEntity.class))
				.thenReturn(List.of(ingestMemberSequenceEntity, ingestMemberSequenceEntity2));

		assertEquals(250, ingestMemberSequenceService.getTotalSequence());
		assertEquals(100, ingestMemberSequenceService.getSequenceForCollection("collectionName"));
		assertEquals(150, ingestMemberSequenceService.getSequenceForCollection("otherCollectionName"));

	}

}