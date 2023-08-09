package be.vlaanderen.informatievlaanderen.ldes.server.fetchmongo.repository;

import be.vlaanderen.informatievlaanderen.ldes.server.fetchmongo.entity.MemberAllocationEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface AllocationEntityRepository extends MongoRepository<MemberAllocationEntity, String> {

	List<MemberAllocationEntity> findAllByFragmentId(String fragmentId);

	void deleteByMemberIdAndCollectionNameAndViewName(String memberId, String collectionName, String viewName);

	void deleteAllByCollectionNameAndViewName(String collectionName, String viewName);

	void deleteAllByCollectionName(String collectionName);
}
