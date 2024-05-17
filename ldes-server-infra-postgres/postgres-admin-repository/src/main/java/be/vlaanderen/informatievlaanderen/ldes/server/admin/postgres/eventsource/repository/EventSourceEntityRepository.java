package be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.eventsource.repository;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.eventsource.entity.EventSourceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventSourceEntityRepository extends JpaRepository<EventSourceEntity, String> {
}
