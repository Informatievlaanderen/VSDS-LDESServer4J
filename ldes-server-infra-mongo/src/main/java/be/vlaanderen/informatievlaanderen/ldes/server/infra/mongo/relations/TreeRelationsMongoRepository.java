package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.relations;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.TreeRelation;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.relations.TreeRelationsRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.relations.entity.TreeRelationsEntity;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.List;

public class TreeRelationsMongoRepository implements TreeRelationsRepository {

	private final MongoTemplate mongoTemplate;

	public TreeRelationsMongoRepository(MongoTemplate mongoTemplate) {
		this.mongoTemplate = mongoTemplate;
	}

	@Override
	public void addTreeRelation(String treeNodeId, TreeRelation relation) {
		Query query = new Query();
		query.addCriteria(Criteria.where("_id").is(treeNodeId));
		Update update = new Update();
		update.addToSet("relations", relation);
		mongoTemplate.upsert(query, update, TreeRelationsEntity.class);
	}

	@Override
	public List<TreeRelation> getRelations(String fragmentId) {
		Query query = new Query();
		query.addCriteria(Criteria.where("_id").is(fragmentId));
		return mongoTemplate.find(query, TreeRelationsEntity.class)
				.stream()
				.findFirst()
				.map(TreeRelationsEntity::getRelations).orElseGet(List::of);
	}

	@Override
	public void deleteTreeRelation(String treeNodeId, TreeRelation relation) {
		Query query = new Query();
		query.addCriteria(Criteria.where("_id").is(treeNodeId));
		Update update = new Update();
		update.pull("relations", relation);
		mongoTemplate.upsert(query, update, TreeRelationsEntity.class);
	}
}
