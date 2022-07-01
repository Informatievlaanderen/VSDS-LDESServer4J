package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.repositories;

import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.entities.LdesFragmentEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.stream.Stream;

public interface LdesFragmentEntityRepository extends MongoRepository<LdesFragmentEntity, String> {
    @Query(value="{'fragmentInfo.collectionName': ?0, 'fragmentInfo.path': ?1, 'fragmentInfo.value' : { $gte: ?2 }}",
            sort="{'fragmentInfo.value': 1}")
    Stream<LdesFragmentEntity> findClosestFragments(String collectionName, String path, String value);

}
