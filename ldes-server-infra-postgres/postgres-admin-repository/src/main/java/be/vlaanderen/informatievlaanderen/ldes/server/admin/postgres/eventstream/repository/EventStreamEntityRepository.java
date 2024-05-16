package be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.eventstream.repository;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.eventstream.entity.EventStreamEntity;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.JpaRepository;

@Primary
public interface EventStreamEntityRepository extends JpaRepository<EventStreamEntity, String> {
}
