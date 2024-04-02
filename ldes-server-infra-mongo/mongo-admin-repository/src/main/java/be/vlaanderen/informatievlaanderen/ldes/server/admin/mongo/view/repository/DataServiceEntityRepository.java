package be.vlaanderen.informatievlaanderen.ldes.server.admin.mongo.view.repository;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.mongo.view.entity.DataServiceEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface DataServiceEntityRepository extends MongoRepository<DataServiceEntity, String> {
    @Query(value = "{'_id':  {$regex:  ?0}}", delete = true)
    void deleteAllByCollectionName(String collectionName);
}
