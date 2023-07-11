package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.fragmentation;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.AllocationRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.fragmentation.entity.AllocationEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.fragmentation.repository.AllocationEntityRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class AllocationMongoRepository implements AllocationRepository {

	private static final Logger log = LoggerFactory.getLogger(AllocationMongoRepository.class);

	private final AllocationEntityRepository repository;

	public AllocationMongoRepository(AllocationEntityRepository repository) {
		this.repository = repository;
	}

	public void allocateMemberToFragment(String memberId, ViewName viewName, String fragmentId) {
		repository.save(new AllocationEntity(new AllocationEntity.AllocationKey(memberId, fragmentId), viewName));
	}

	public void unallocateMemberFromView(String memberId, ViewName viewName) {
		repository.deleteByAllocationKey_MemberIdAndViewName_CollectionName(memberId, viewName.getCollectionName());
	}

	public void unallocateAllMembersFromView(ViewName viewName) {
		repository.deleteAllByViewName(viewName);
	}

	public void unallocateMembersFromCollection(String collectionName) {
		repository.deleteAllByViewName_CollectionName(collectionName);
	}

	public List<String> findMemberIdsForFragment(String fragmentId) {
		return repository.findAllByAllocationKey_FragmentId(fragmentId)
				.map(AllocationEntity::getAllocationKey)
				.map(AllocationEntity.AllocationKey::memberId)
				.toList();
	}
}
