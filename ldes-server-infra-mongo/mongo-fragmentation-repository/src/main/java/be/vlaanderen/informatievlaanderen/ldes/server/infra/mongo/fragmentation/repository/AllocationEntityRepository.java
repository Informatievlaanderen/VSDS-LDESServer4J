package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.fragmentation.repository;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.fragmentation.entity.AllocationEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.stream.Stream;

public interface AllocationEntityRepository extends MongoRepository<AllocationEntity, AllocationEntity.AllocationKey> {
	Stream<AllocationEntity> findAllByFragmentId(String fragmentId);

	void deleteAllByAllocationKey_ViewName(ViewName viewName);

	void deleteAllByAllocationKey_ViewName_CollectionName(String collectionName);
}