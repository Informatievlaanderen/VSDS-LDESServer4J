package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.LdesFragmentIdentifier;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.BucketisedMember;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Fragment;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.FragmentationMember;
import io.micrometer.observation.Observation;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class FragmentationStrategyImplTest {
	private static final String MEMBER_ID = "memberId";
	private static final ViewName VIEW_NAME = new ViewName("collectionName", "view");
	private static final LdesFragmentIdentifier FRAGMENT_ID = new LdesFragmentIdentifier(VIEW_NAME, List.of());
	private final FragmentationStrategyImpl fragmentationStrategy = new FragmentationStrategyImpl();

	@Test
	void when_memberIsAddedToFragment_FragmentationStrategyImplSavesUpdatedFragment() {
		Fragment fragment = new Fragment(FRAGMENT_ID);
		FragmentationMember member = mock(FragmentationMember.class);
		when(member.id()).thenReturn(MEMBER_ID);

		List<BucketisedMember> members = fragmentationStrategy.addMemberToFragment(fragment, member, mock(Observation.class));

		assertThat(members).hasSize(1);
		assertEquals(MEMBER_ID, members.getFirst().memberId());
		assertEquals(VIEW_NAME, members.getFirst().viewName());
		assertEquals(FRAGMENT_ID.asDecodedFragmentId(), members.getFirst().fragmentId());
	}
}
