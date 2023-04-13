package be.vlaanderen.informatievlaanderen.ldes.server.domain.snapshot.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.RootFragmentCreator;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.valueobjects.FragmentPair;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.snapshot.entities.Snapshot;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.LdesConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

class SnapshotCreatorImplTest {

	private final MemberCollector memberCollector = mock(MemberCollector.class);
	private final RootFragmentCreator rootFragmentCreator = mock(RootFragmentCreator.class);
	private final SnapshotFragmenter snapshotFragmenter = mock(SnapshotFragmenter.class);
	private SnapShotCreator snapShotCreator;
	private LdesConfig ldesConfig;

	@BeforeEach
	void setUp() {
		ldesConfig = new LdesConfig();
		ldesConfig.setHostName("localhost:8080");
		ldesConfig.setCollectionName("collection");
		ldesConfig.validation().setShape("shape");
		snapShotCreator = new SnapshotCreatorImpl(memberCollector, rootFragmentCreator, snapshotFragmenter);
	}

	@Test
	void when_SnapshotIsCreated_MembersAreCollectedAndFragmentedForSnapshot() {
		List<LdesFragment> ldesFragmentsForSnapshot = getLdesFragmentsForSnapshot();
		Map<String, List<Member>> membersOfSnapshot = getMembers();
		when(memberCollector.getMembersGroupedByVersionOf(ldesFragmentsForSnapshot)).thenReturn(membersOfSnapshot);
		LdesFragment rootFragmentOfSnapshot = new LdesFragment("collectionName", "snapshot-", List.of());
		when(rootFragmentCreator.createRootFragmentForView(contains("snapshot-"))).thenReturn(rootFragmentOfSnapshot);

		Snapshot snapshot = snapShotCreator.createSnapshotForTreeNodes(ldesFragmentsForSnapshot, ldesConfig);

		InOrder inOrder = inOrder(memberCollector, rootFragmentCreator, snapshotFragmenter);
		inOrder.verify(memberCollector, times(1)).getMembersGroupedByVersionOf(ldesFragmentsForSnapshot);
		inOrder.verify(rootFragmentCreator, times(1)).createRootFragmentForView(contains("snapshot-"));
		inOrder.verify(snapshotFragmenter, times(1))
				.fragmentSnapshotMembers(getMemberLastVersionsOfSnapshot(membersOfSnapshot), rootFragmentOfSnapshot);
		inOrder.verifyNoMoreInteractions();
		assertTrue(snapshot.getSnapshotId().contains("snapshot-"));
		assertEquals("localhost:8080/collection", snapshot.getSnapshotOf());
		assertTrue(snapshot.getSnapshotUntil().isBefore(LocalDateTime.now()));
		assertEquals("shape", snapshot.getShape());
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
		return new Member("collectionName", memberId, versionOf, LocalDateTime.of(1, 1, 1, 1, minute), null, List.of());
	}

	private List<LdesFragment> getLdesFragmentsForSnapshot() {
		LdesFragment ldesFragment = new LdesFragment("collectionName", "view", List.of());
		LdesFragment ldesFragment1 = new LdesFragment("collectionName", "view", List.of(new FragmentPair("page", "1")));
		LdesFragment ldesFragment2 = new LdesFragment("collectionName", "view", List.of(new FragmentPair("page", "2")));
		return List.of(ldesFragment, ldesFragment1, ldesFragment2);
	}
}