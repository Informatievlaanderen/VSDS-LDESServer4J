package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.TreeRelation;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.treenoderelations.TreeNodeRelationsRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.entities.TreeNodeRelationsEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.List;

public class TreeNodeRelationsMongoRepository implements TreeNodeRelationsRepository {

    public TreeNodeRelationsMongoRepository() {

    }

    @Autowired
    MongoTemplate mongoTemplate;

    @Override
    public void addTreeNodeRelation(String treeNodeId, TreeRelation relation) {
            Query query = new Query();
            query.addCriteria(Criteria.where("_id").is(treeNodeId));
            Update update = new Update();
            update.addToSet("relations", relation);
            mongoTemplate.upsert(query, update, TreeNodeRelationsEntity.class);
    }

    @Override
    public List<TreeRelation> getRelations(String fragmentId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(fragmentId));
       return mongoTemplate.find(query, TreeNodeRelationsEntity.class)
               .stream()
               .findFirst()
               .map(TreeNodeRelationsEntity::getRelations).orElseGet(List::of);
    }
}
