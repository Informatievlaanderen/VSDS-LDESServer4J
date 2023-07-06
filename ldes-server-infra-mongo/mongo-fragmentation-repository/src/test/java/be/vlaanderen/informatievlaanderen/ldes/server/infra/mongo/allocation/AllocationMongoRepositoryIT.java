package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.allocation;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.fragmentation.AllocationMongoRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.fragmentation.entity.AllocationEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.fragmentation.entity.AllocationEntity.AllocationKey;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.fragmentation.repository.AllocationEntityRepository;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

public class AllocationMongoRepositoryIT {

	private final AllocationEntityRepository allocationEntityRepository = mock(AllocationEntityRepository.class);
	private final AllocationMongoRepository allocationMongoRepository = new AllocationMongoRepository(
			allocationEntityRepository);

	@Test
	void when_AllocateMemberToFragment_AllocationIsSaved() {
		String memberId = "memberId";
		String fragmentId = "fragmentId";
		ViewName viewName = ViewName.fromString("collection" + "/" + "view");
		AllocationEntity allocation = new AllocationEntity(new AllocationKey(memberId, viewName), fragmentId);
		allocationMongoRepository.allocateMemberToFragment(memberId, viewName, fragmentId);

		verify(allocationEntityRepository, times(1)).save(eq(allocation));
	}

	@Test
	void when_UnallocateMemberFromView_AllocationIsSaved() {
		String memberId = "memberId";
		ViewName viewName = ViewName.fromString("collection" + "/" + "view");
		AllocationKey allocation = new AllocationKey(memberId, viewName);
		allocationMongoRepository.unallocateMemberFromView(memberId, viewName);

		verify(allocationEntityRepository, times(1)).deleteById(eq(allocation));
	}

}
