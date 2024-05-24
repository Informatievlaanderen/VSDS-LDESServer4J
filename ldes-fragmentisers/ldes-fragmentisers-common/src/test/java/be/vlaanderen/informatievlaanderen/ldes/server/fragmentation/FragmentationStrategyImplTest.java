package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.LdesFragmentIdentifier;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.BucketisedMember;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Fragment;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.FragmentRepository;
import io.micrometer.observation.Observation;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class FragmentationStrategyImplTest {
	private static final String MEMBER_ID = "memberId";
	private static final long SEQ_NR = 5L;
	private static final ViewName VIEW_NAME = new ViewName("collectionName", "view");
	private static final LdesFragmentIdentifier FRAGMENT_ID = new LdesFragmentIdentifier(VIEW_NAME, List.of());
	private final FragmentRepository fragmentRepository = mock(FragmentRepository.class);
	private final ApplicationEventPublisher eventPublisher = mock(ApplicationEventPublisher.class);

	private final FragmentationStrategyImpl fragmentationStrategy = new FragmentationStrategyImpl(
			fragmentRepository,
			eventPublisher);

	@Test
	void when_memberIsAddedToFragment_FragmentationStrategyImplSavesUpdatedFragment() {
		Fragment fragment = new Fragment(FRAGMENT_ID);
		Member member = mock(Member.class);
		when(member.id()).thenReturn(MEMBER_ID);
		when(member.sequenceNr()).thenReturn(SEQ_NR);

		List<BucketisedMember> members = fragmentationStrategy.addMemberToFragment(fragment, member, mock(Observation.class));

		assertThat(members).hasSize(1);
		assertThat(members.getFirst()).hasFieldOrPropertyWithValue("memberId", MEMBER_ID)
				.hasFieldOrPropertyWithValue("viewName", VIEW_NAME)
				.hasFieldOrPropertyWithValue("fragmentId", FRAGMENT_ID.asDecodedFragmentId())
				.hasFieldOrPropertyWithValue("sequenceNr", SEQ_NR);
	}
}
