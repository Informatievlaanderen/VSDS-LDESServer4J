package be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.eventsource.v2.repository;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.eventsource.v2.entity.EventSourceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface EventSourceEntityRepository extends JpaRepository<EventSourceEntity, Integer> {
    @Query("SELECT e FROM EventSourceEntity e WHERE e.eventStream.name = :collectionName")
    Optional<EventSourceEntity> findByCollectionName(String collectionName);

    @Query("DELETE FROM EventSourceEntity e WHERE e.eventStream.name = :collectionName")
    void deleteByCollectionName(String collectionName);
}
