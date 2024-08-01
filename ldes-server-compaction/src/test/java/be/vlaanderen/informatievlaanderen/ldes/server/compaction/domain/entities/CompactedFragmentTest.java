package be.vlaanderen.informatievlaanderen.ldes.server.compaction.domain.entities;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.TreeRelation;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Fragment;

import java.util.List;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.model.LdesFragmentIdentifier.fromFragmentId;

class CompactedFragmentTest {

	private static final String MOBHIND_PAGE = "/mobility-hindrances/by-page?pageNumber=";

	private CompactedFragmentCreator compactedFragment;

//	@BeforeEach
//	void setUp() {
//		Set<CompactionCandidate> toBeCompactedFragments = Stream.of(
//				AffectedAllocationAggregate.of(MOBHIND_PAGE + "2", List.of(MOBHIND_PAGE + "3")),
//				AffectedAllocationAggregate.of(MOBHIND_PAGE + "1", List.of(MOBHIND_PAGE + "2")),
//				AffectedAllocationAggregate.of(MOBHIND_PAGE + "3", List.of(MOBHIND_PAGE + "4"))
//		).map(affectedAllocationAggregate -> {
//			var cc = affectedAllocationAggregate.compactionCandidate;
//			cc.setFragment(affectedAllocationAggregate.fragment);
//			return cc;
//		}).collect(Collectors.toSet());
//
//		compactedFragment = new CompactedFragment(toBeCompactedFragments);
//	}
//
//	@Test
//	void getFragment() {
//		Fragment fragment = compactedFragment.getFragment();
//
//		assertEquals(MOBHIND_PAGE + "1/2/3", fragment.getFragmentIdString());
//		assertTrue(fragment.isImmutable());
//		assertEquals(6, fragment.getNrOfMembersAdded());
//		assertNull(fragment.getDeleteTime());
//		assertEquals(1, fragment.getRelations().size());
//		assertEquals(MOBHIND_PAGE + "4", fragment.getRelations().get(0).treeNode().asDecodedFragmentId());
//	}
//
//	@Test
//	void getImpactedFragmentIds() {
//		assertTrue(compactedFragment.getImpactedFragmentIds()
//				.containsAll(List.of(MOBHIND_PAGE + "1",
//						MOBHIND_PAGE + "2",
//						MOBHIND_PAGE + "3")));
//	}
//
//	@Test
//	void getImpactedFragmentIdentifiers() {
//		assertTrue(compactedFragment.getImpactedFragmentIdentifiers()
//				.containsAll(List.of(fromFragmentId(MOBHIND_PAGE + "1"),
//						fromFragmentId(MOBHIND_PAGE + "2"),
//						fromFragmentId(MOBHIND_PAGE + "3"))));
//	}
//
//	@Test
//	void getFirstImpactedFragment() {
//		assertEquals(MOBHIND_PAGE + "1",
//				compactedFragment.getFirstImpactedFragment().getFragmentId().asDecodedFragmentId());
//	}

	private static Fragment createFragment(String fragmentId, List<String> fragmentsPointingTo) {
		return new Fragment(fromFragmentId(fragmentId), true, 10,
				fragmentsPointingTo.stream()
						.map(treeNode -> new TreeRelation(null, fromFragmentId(treeNode), null, null, null))
						.toList(), null);
	}

//	record AffectedAllocationAggregate(CompactionCandidate compactionCandidate, Fragment fragment) {
//		static AffectedAllocationAggregate of(String fragmentId, List<String> fragmentsPointingTo) {
//			return new AffectedAllocationAggregate(new CompactionCandidate(fragmentId, 2),
//					createFragment(fragmentId, fragmentsPointingTo));
//		}
//	}

}