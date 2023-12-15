package be.vlaanderen.informatievlaanderen.ldes.server.compaction.domain.entities;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.LdesFragmentIdentifier;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.TreeRelation;
import be.vlaanderen.informatievlaanderen.ldes.server.fetching.entities.AllocationAggregate;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Fragment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.model.LdesFragmentIdentifier.fromFragmentId;
import static org.junit.jupiter.api.Assertions.assertEquals;

class CompactedFragmentTest {

	private static final String MOBHIND_PAGE = "/mobility-hindrances/by-page?pageNumber=";

	private CompactedFragment compactedFragment;

	@BeforeEach
	void setUp() {
		var toBeCompactedFragments = Stream.of(
				AffectedAllocationAggregate.of(MOBHIND_PAGE + "2", List.of(MOBHIND_PAGE + "3")),
				AffectedAllocationAggregate.of(MOBHIND_PAGE + "1", List.of(MOBHIND_PAGE + "2")),
				AffectedAllocationAggregate.of(MOBHIND_PAGE + "3", List.of(MOBHIND_PAGE + "4"))
		).map(affectedAllocationAggregate -> {
			var ag = affectedAllocationAggregate.allocationAggregate;
			ag.setFragment(new Fragment(LdesFragmentIdentifier.fromFragmentId(ag.getId())));
			return ag;
		}).collect(Collectors.toSet());

		compactedFragment = new CompactedFragment(toBeCompactedFragments);
	}

	@Test
	void getLdesFragmentIdentifier() {
	}

	@Test
	void getFragment() {
		Fragment fragment = compactedFragment.getFragment();

		assertEquals(MOBHIND_PAGE+"1/2/3", fragment.getFragmentIdString());

		assertEquals(1, fragment.getRelations().size());
		assertEquals(MOBHIND_PAGE+"4", fragment.getRelations().get(0).treeNode().asString());
	}

	@Test
	void getImpactedFragmentIds() {
	}

	@Test
	void getImpactedFragmentIdentifiers() {
	}

	@Test
	void getFirstImpactedFragment() {
	}

	private static Fragment createFragment(String fragmentId, List<String> fragmentsPointingTo) {
		return new Fragment(fromFragmentId(fragmentId), true, 10,
				fragmentsPointingTo.stream()
						.map(treeNode -> new TreeRelation(null, fromFragmentId(treeNode), null, null, null))
						.toList(), null);
	}

	record AffectedAllocationAggregate(AllocationAggregate allocationAggregate, Fragment fragment) {
		static AffectedAllocationAggregate of(String fragmentId, List<String> fragmentsPointingTo) {
			return new AffectedAllocationAggregate(new AllocationAggregate(fragmentId, 0),
					createFragment(fragmentId, fragmentsPointingTo));
		}
	}

	;

}