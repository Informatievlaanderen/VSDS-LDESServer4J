package be.vlaanderen.informatievlaanderen.ldes.server.snapshot.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.LdesFragmentIdentifier;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.FragmentationStrategy;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Fragment;
import be.vlaanderen.informatievlaanderen.ldes.server.snapshot.entities.Member;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class SnapshotFragmenterImplTest {

	private final FragmentationStrategy fragmentationStrategy = Mockito.mock(FragmentationStrategy.class);
	private final SnapshotFragmenter snapshotFragmenter = new SnapshotFragmenterImpl(fragmentationStrategy,
			ObservationRegistry.create());

	@Test
	void when_MembersAreSentForFragmentation_TheyAreFragmentedOneByOne() {
		Set<Member> members = Set.of(new Member("id", null, null, null));
		Fragment rootTreeNode = new Fragment(
				new LdesFragmentIdentifier(new ViewName("collectionName", "view"), List.of()));

		snapshotFragmenter.fragmentSnapshotMembers(members, rootTreeNode);

		members.forEach(member -> verify(fragmentationStrategy, times(1))
				.addMemberToFragment(eq(rootTreeNode),
						eq(member.id()), eq(member.model()), any(Observation.class)));
		Mockito.verifyNoMoreInteractions(fragmentationStrategy);
	}

}