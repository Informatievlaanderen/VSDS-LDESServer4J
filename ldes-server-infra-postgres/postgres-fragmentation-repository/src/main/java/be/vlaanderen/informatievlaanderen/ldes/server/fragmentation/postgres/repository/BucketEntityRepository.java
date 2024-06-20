package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.postgres.repository;

import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.postgres.entity.BucketEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BucketEntityRepository extends JpaRepository<BucketEntity, Long> {

}
