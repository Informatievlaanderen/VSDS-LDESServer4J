package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.relations;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.TreeRelation;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.relations.entity.TreeRelationsEntity;
import org.junit.jupiter.api.Test;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class TreeRelationsMongoRepositoryTest {

	private final MongoTemplate mongoTemplate = mock(MongoTemplate.class);

	private final TreeRelationsMongoRepository treeRelationsMongoRepository = new TreeRelationsMongoRepository(
			mongoTemplate);

	@Test
	void when_TreeRelationIsAdded_CorrectMongoQueryIsExecuted() {
		TreeRelation treeRelation = new TreeRelation("path", "node", "value", "valueType", "relation");
		Query query = new Query();
		query.addCriteria(Criteria.where("_id").is("treeNodeId"));
		Update update = new Update();
		update.addToSet("relations", treeRelation);

		treeRelationsMongoRepository.addTreeRelation("treeNodeId", treeRelation);

		verify(mongoTemplate, times(1)).upsert(query, update, TreeRelationsEntity.class);
	}

	@Test
	void when_TreeRelationAreRetrieved_CorrectMongoQueryIsExecuted() {
		List<TreeRelation> expectedTreeRelations = List
				.of(new TreeRelation("path", "node", "value", "valueType", "relation"));
		Query query = new Query();
		query.addCriteria(Criteria.where("_id").is("treeNodeId"));
		when(mongoTemplate.find(query, TreeRelationsEntity.class))
				.thenReturn(List.of(new TreeRelationsEntity("treeNodeId", expectedTreeRelations)));

		List<TreeRelation> actualTreeRelations = treeRelationsMongoRepository.getRelations("treeNodeId");

		assertEquals(expectedTreeRelations, actualTreeRelations);
	}

	@Test
	void when_TreeRelationIsDeleted_CorrectMongoQueryIsExecuted() {
		TreeRelation treeRelation = new TreeRelation("path", "node", "value", "valueType", "relation");
		Query query = new Query();
		query.addCriteria(Criteria.where("_id").is("treeNodeId"));
		Update update = new Update();
		update.pull("relations", treeRelation);

		treeRelationsMongoRepository.deleteTreeRelation("treeNodeId", treeRelation);

		verify(mongoTemplate, times(1)).upsert(query, update, TreeRelationsEntity.class);
	}

}