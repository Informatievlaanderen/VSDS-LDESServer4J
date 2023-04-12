package be.vlaanderen.informatievlaanderen.ldes.server.domain.snapshot.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentationStrategy;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.entities.Member;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class SnapshotFragmenterImplTest {

	private final FragmentationStrategy fragmentationStrategy = mock(FragmentationStrategy.class);
	private final SnapshotFragmenter snapshotFragmenter = new SnapshotFragmenterImpl(fragmentationStrategy,
			ObservationRegistry.create());

	@Test
	void when_MembersAreSentForFragmentation_TheyAreFragmentedOneByOne() {
		Set<Member> members = Set.of(new Member("id", "collection", 0L, null, null, null, List.of()));
		LdesFragment rootTreeNode = new LdesFragment("view", List.of());

		snapshotFragmenter.fragmentSnapshotMembers(members, rootTreeNode);

		members.forEach(member -> verify(fragmentationStrategy, times(1)).addMemberToFragment(eq(rootTreeNode),
				eq(member), any(Observation.class)));
		verifyNoMoreInteractions(fragmentationStrategy);
	}

}