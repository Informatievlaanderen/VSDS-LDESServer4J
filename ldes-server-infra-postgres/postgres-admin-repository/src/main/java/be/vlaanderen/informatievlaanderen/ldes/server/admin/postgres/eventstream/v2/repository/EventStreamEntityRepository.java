package be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.eventstream.v2.repository;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.eventstream.v2.entity.EventStreamEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository("collectionsRepository")
public interface EventStreamEntityRepository extends JpaRepository<EventStreamEntity, Short> {
    Optional<EventStreamEntity> findByName(String name);
    void deleteByName(String name);
}
