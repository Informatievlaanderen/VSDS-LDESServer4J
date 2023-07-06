package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.fragmentation.repository;

import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.fragmentation.entity.AllocationEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AllocationEntityRepository extends MongoRepository<AllocationEntity, AllocationEntity.AllocationKey> {
	@Override void deleteById(AllocationEntity.AllocationKey allocationKey);
}
