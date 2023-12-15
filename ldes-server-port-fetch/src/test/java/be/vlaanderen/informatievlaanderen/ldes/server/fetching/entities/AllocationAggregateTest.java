package be.vlaanderen.informatievlaanderen.ldes.server.fetching.entities;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.LdesFragmentIdentifier;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Fragment;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AllocationAggregateTest {

	@Test
	void getFragmentWhenNotInitialised() {
		String fragmentId = "/event-stream/view";
		AllocationAggregate allocationAggregate = new AllocationAggregate(fragmentId, 10);
		assertThrows(RuntimeException.class, allocationAggregate::getFragment);

		Fragment fragment = new Fragment(LdesFragmentIdentifier.fromFragmentId(fragmentId));

		allocationAggregate.setFragment(fragment);
		assertEquals(fragment, allocationAggregate.getFragment());
	}

}