package be.vlaanderen.informatievlaanderen.ldes.server.snapshot.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.LdesFragmentIdentifier;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.valueobjects.FragmentPair;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.shacl.entities.ShaclShape;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.shacl.services.ShaclShapeService;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.snapshot.Snapshot;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.factory.RootFragmentCreator;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Fragment;
import be.vlaanderen.informatievlaanderen.ldes.server.snapshot.entities.Member;
import org.apache.jena.rdf.model.ModelFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class SnapshotCreatorImplTest {

	private final MemberCollector memberCollector = Mockito.mock(MemberCollector.class);
	private final RootFragmentCreator rootFragmentCreator = Mockito.mock(RootFragmentCreator.class);
	private final SnapshotFragmenter snapshotFragmenter = Mockito.mock(SnapshotFragmenter.class);
	private final ShaclShapeService shaclShapeService = mock(ShaclShapeService.class);
	private SnapShotCreator snapShotCreator;

	@BeforeEach
	void setUp() {
		snapShotCreator = new SnapshotCreatorImpl("localhost:8080", memberCollector, rootFragmentCreator,
				snapshotFragmenter,
				shaclShapeService);
	}

	@Disabled("To be enabled when snapshotting becomes functional again")
	@Test
	void when_SnapshotIsCreated_MembersAreCollectedAndFragmentedForSnapshot() {
		List<Fragment> fragmentsForSnapshot = getLdesFragmentsForSnapshot();
		Map<String, List<Member>> membersOfSnapshot = getMembers();
		when(memberCollector.getMembersGroupedByVersionOf(fragmentsForSnapshot)).thenReturn(membersOfSnapshot);
		Fragment rootFragmentOfSnapshot = new Fragment(
				new LdesFragmentIdentifier(new ViewName("collectionName", "snapshot-"), List.of()));
		when(rootFragmentCreator.createRootFragmentForView(any())).thenReturn(rootFragmentOfSnapshot);

		when(shaclShapeService.retrieveShaclShape("collection"))
				.thenReturn(new ShaclShape("collection", ModelFactory.createDefaultModel()));
		Snapshot snapshot = snapShotCreator.createSnapshotForTreeNodes(fragmentsForSnapshot, "collection");

		InOrder inOrder = inOrder(memberCollector, rootFragmentCreator, snapshotFragmenter);
		inOrder.verify(memberCollector, times(1)).getMembersGroupedByVersionOf(fragmentsForSnapshot);
		inOrder.verify(rootFragmentCreator, times(1)).createRootFragmentForView(any());
		inOrder.verify(snapshotFragmenter, times(1))
				.fragmentSnapshotMembers(getMemberLastVersionsOfSnapshot(membersOfSnapshot), rootFragmentOfSnapshot);
		inOrder.verifyNoMoreInteractions();
		assertTrue(snapshot.getSnapshotId().contains("snapshot-"));
		assertEquals("localhost:8080/collection", snapshot.getSnapshotOf());
		assertTrue(snapshot.getSnapshotUntil().isBefore(LocalDateTime.now()));
		assertTrue(snapshot.getShape().isIsomorphicWith(ModelFactory.createDefaultModel()));
	}

	private Set<Member> getMemberLastVersionsOfSnapshot(Map<String, List<Member>> membersOfSnapshot) {
		return Set.of(membersOfSnapshot.get("id1").get(2), membersOfSnapshot.get("id2").get(1));
	}

	private Map<String, List<Member>> getMembers() {
		return Map.of("id1",
				List.of(createMember("id1/1", "id1", 1), createMember("id1/2", "id1", 2),
						createMember("id1/3", "id1", 3)),
				"id2", List.of(createMember("id2/1", "id2", 1), createMember("id2/2", "id2", 2)));
	}

	private Member createMember(String memberId, String versionOf, int minute) {
		return new Member(memberId, null, versionOf, LocalDateTime.of(1, 1, 1, 1, minute));
	}

	private List<Fragment> getLdesFragmentsForSnapshot() {
		Fragment fragment = new Fragment(
				new LdesFragmentIdentifier(new ViewName("collectionName", "view"), List.of()));
		Fragment fragment1 = new Fragment(new LdesFragmentIdentifier(new ViewName("collectionName", "view"),
				List.of(new FragmentPair("page", "1"))));
		Fragment fragment2 = new Fragment(new LdesFragmentIdentifier(new ViewName("collectionName", "view"),
				List.of(new FragmentPair("page", "2"))));
		return List.of(fragment, fragment1, fragment2);
	}
}