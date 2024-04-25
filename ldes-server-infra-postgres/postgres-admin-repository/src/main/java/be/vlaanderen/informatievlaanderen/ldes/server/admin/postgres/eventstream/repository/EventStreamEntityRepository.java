package be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.eventstream.repository;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.eventstream.entity.EventStreamEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventStreamEntityRepository extends JpaRepository<EventStreamEntity, String> {
}
