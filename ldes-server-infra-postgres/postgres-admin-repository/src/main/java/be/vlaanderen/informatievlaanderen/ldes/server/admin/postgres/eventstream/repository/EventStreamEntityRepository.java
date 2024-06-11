package be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.eventstream.repository;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.eventstream.entity.EventStreamEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.eventstream.projection.EventStreamProperties;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventStreamEntityRepository extends JpaRepository<EventStreamEntity, Integer> {
	List<EventStreamProperties> findAllPropertiesBy();

	Optional<EventStreamEntity> findByName(String name);

	Optional<EventStreamProperties> findPropertiesByName(String name);

	@Modifying
	@Query("DELETE FROM EventStreamEntity e WHERE e.name = :name")
	int deleteByName(String name);

    @Modifying
    @Query("update EventStreamEntity e set e.isClosed = true WHERE e.id = :id")
    void closeEventStream(@Param("id") String collectionName);
}
