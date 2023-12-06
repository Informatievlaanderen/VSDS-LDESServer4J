package be.vlaanderen.informatievlaanderen.ldes.server.fetch.repository;

import be.vlaanderen.informatievlaanderen.ldes.server.fetch.entity.MemberAllocationEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Set;

public interface AllocationEntityRepository extends MongoRepository<MemberAllocationEntity, String> {

	List<MemberAllocationEntity> findAllByFragmentId(String fragmentId);

	void deleteByMemberIdAndCollectionNameAndViewName(String memberId, String collectionName, String viewName);

	void deleteAllByCollectionNameAndViewName(String collectionName, String viewName);

	void deleteAllByCollectionName(String collectionName);

	void deleteAllByFragmentId(String fragmentId);

	void deleteAllByFragmentIdIn(Set<String> fragmentIds);

    long countByCollectionNameAndViewName(String collectionName, String viewName);
}
