package be.vlaanderen.informatievlaanderen.ldes.server.fetch;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.fetch.entity.AllocationEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.fetch.repository.AllocationEntityRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.fetching.repository.AllocationRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AllocationMongoRepository implements AllocationRepository {

	private final AllocationEntityRepository repository;

	public AllocationMongoRepository(AllocationEntityRepository repository) {
		this.repository = repository;
	}

	public void allocateMemberToFragment(String memberId, ViewName viewName, String fragmentId) {
		repository.save(new AllocationEntity(new AllocationEntity.AllocationKey(memberId, fragmentId), viewName));
	}

	public void unallocateMemberFromView(String memberId, ViewName viewName) {
		repository.deleteByAllocationKey_MemberIdAndViewName(memberId, viewName);
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
