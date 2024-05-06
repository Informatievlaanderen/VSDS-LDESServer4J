package be.vlaanderen.informatievlaanderen.ldes.server.retention;

import be.vlaanderen.informatievlaanderen.ldes.server.retention.entities.MemberPropertiesEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.stream.Stream;

public interface MemberPropertiesEntityRepository extends MongoRepository<MemberPropertiesEntity, String> {

    Stream<MemberPropertiesEntity> findMemberPropertiesEntitiesByCollectionNameAndViewsContainingAndTimestampBefore(
            String collectionName, String viewName, LocalDateTime timestamp);
    Stream<MemberPropertiesEntity> findMemberPropertiesEntitiesByCollectionNameAndTimestampBefore(
            String collectionName, LocalDateTime timestamp);
}
