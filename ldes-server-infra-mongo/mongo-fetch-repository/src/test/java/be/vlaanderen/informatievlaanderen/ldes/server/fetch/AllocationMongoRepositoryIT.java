package be.vlaanderen.informatievlaanderen.ldes.server.fetch;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.fetch.entity.MemberAllocationEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.fetch.mapper.MemberAllocationEntityMapper;
import be.vlaanderen.informatievlaanderen.ldes.server.fetch.repository.AllocationEntityRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.fetching.entities.MemberAllocation;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

class AllocationMongoRepositoryIT {

	private final AllocationEntityRepository allocationEntityRepository = mock(AllocationEntityRepository.class);
	private final MemberAllocationEntityMapper mapper = new MemberAllocationEntityMapper();
	private final AllocationMongoRepository allocationMongoRepository = new AllocationMongoRepository(
			allocationEntityRepository, mapper);

	@Test
	void when_AllocateMemberToFragment_AllocationIsSaved() {
		MemberAllocation memberAllocation = new MemberAllocation("id", "collectionName", "viewName", "fragmentId",
				"memberId");
		MemberAllocationEntity allocation = mapper.toMemberAllocationEntity(memberAllocation);

		allocationMongoRepository.saveAllocation(memberAllocation);

		// verify(allocationEntityRepository, times(1)).save(allocation);
	}

	// @Test
	// void when_UnallocateMemberFromView_AllocationIsSaved() {
	// String memberId = "memberId";
	// ViewName viewName = ViewName.fromString("collection" + "/" + "view");
	// allocationMongoRepository.deleteByMemberIdAndCollectionNameAndViewName(memberId,
	// viewName);
	//
	// verify(allocationEntityRepository, times(1))
	// .deleteByAllocationKey_MemberIdAndViewName(memberId, viewName);
	// }

}
