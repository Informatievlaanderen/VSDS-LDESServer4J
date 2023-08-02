package be.vlaanderen.informatievlaanderen.ldes.server.fetch;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.fetch.entity.AllocationEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.fetch.repository.AllocationEntityRepository;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

class AllocationMongoRepositoryIT {

	private final AllocationEntityRepository allocationEntityRepository = mock(AllocationEntityRepository.class);
	private final AllocationMongoRepository allocationMongoRepository = new AllocationMongoRepository(
			allocationEntityRepository);

	@Test
	void when_AllocateMemberToFragment_AllocationIsSaved() {
		String memberId = "memberId";
		String fragmentId = "fragmentId";
		ViewName viewName = ViewName.fromString("collection" + "/" + "view");
		AllocationEntity allocation = new AllocationEntity(new AllocationEntity.AllocationKey(memberId, fragmentId),
				viewName);
		allocationMongoRepository.allocateMemberToFragment(memberId, viewName, fragmentId);

		verify(allocationEntityRepository, times(1)).save(allocation);
	}

	@Test
	void when_UnallocateMemberFromView_AllocationIsSaved() {
		String memberId = "memberId";
		ViewName viewName = ViewName.fromString("collection" + "/" + "view");
		allocationMongoRepository.unallocateMemberFromView(memberId, viewName);

		verify(allocationEntityRepository, times(1))
				.deleteByAllocationKey_MemberIdAndViewName(memberId, viewName);
	}

}
