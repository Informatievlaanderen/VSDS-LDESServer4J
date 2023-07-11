package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.fragmentation.repository;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.fragmentation.entity.AllocationEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.stream.Stream;

public interface AllocationEntityRepository extends MongoRepository<AllocationEntity, AllocationEntity.AllocationKey> {
	Stream<AllocationEntity> findAllByAllocationKey_FragmentId(String fragmentId);

	void deleteByAllocationKey_MemberIdAndViewName_CollectionName(String memberId, String collectionName);

	void deleteAllByViewName(ViewName viewName);

	void deleteAllByViewName_CollectionName(String collectionName);
}
