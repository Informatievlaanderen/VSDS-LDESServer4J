package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.fragmentation.repository;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.fragmentation.entity.AllocationEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AllocationEntityRepository extends MongoRepository<AllocationEntity, AllocationEntity.AllocationKey> {

	void deleteAllByAllocationKey_ViewName(ViewName viewName);

	void deleteAllByAllocationKey_ViewName_CollectionName(String collectionName);
}
