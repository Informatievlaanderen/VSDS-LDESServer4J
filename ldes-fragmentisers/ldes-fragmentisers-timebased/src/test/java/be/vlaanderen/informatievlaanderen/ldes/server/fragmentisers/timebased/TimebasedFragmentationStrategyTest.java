package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebased;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.LdesFragmentIdentifier;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.valueobjects.FragmentPair;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.FragmentationStrategy;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.FragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebased.services.OpenFragmentProvider;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

import java.util.List;

import static org.mockito.Mockito.*;

class TimebasedFragmentationStrategyTest {

	private static final ViewName VIEW_NAME = new ViewName("collectionName", "view");
	private final OpenFragmentProvider openFragmentProvider = mock(OpenFragmentProvider.class);
	private final FragmentationStrategy decoratedFragmentationStrategy = mock(FragmentationStrategy.class);
	private FragmentationStrategy fragmentationStrategy;
	private static LdesFragment PARENT_FRAGMENT;
	private static LdesFragment OPEN_FRAGMENT;
	private final FragmentRepository fragmentRepository = mock(FragmentRepository.class);

	@BeforeEach
	void setUp() {
		PARENT_FRAGMENT = new LdesFragment(new LdesFragmentIdentifier(VIEW_NAME, List.of()));
		OPEN_FRAGMENT = PARENT_FRAGMENT.createChild(new FragmentPair("generatedAtTime", "someTime"));
		fragmentationStrategy = new TimebasedFragmentationStrategy(decoratedFragmentationStrategy,
				openFragmentProvider, ObservationRegistry.create(),
				fragmentRepository);
	}

	@Test
	void when_MemberIsAddedToFragment_TimebasedFragmentationIsApplied() {
		Member member = mock(Member.class);
		when(member.getLdesMemberId()).thenReturn("memberId");
		when(openFragmentProvider.retrieveOpenFragmentOrCreateNewFragment(PARENT_FRAGMENT))
				.thenReturn(new ImmutablePair<>(OPEN_FRAGMENT, false));

		fragmentationStrategy.addMemberToFragment(PARENT_FRAGMENT,
				member.getLdesMemberId(), member.getModel(), any(Observation.class));

		InOrder inOrder = inOrder(openFragmentProvider, fragmentRepository,
				decoratedFragmentationStrategy);
		inOrder.verify(openFragmentProvider,
				times(1)).retrieveOpenFragmentOrCreateNewFragment(PARENT_FRAGMENT);
		inOrder.verify(decoratedFragmentationStrategy,
				times(1)).addMemberToFragment(eq(OPEN_FRAGMENT), eq(member.getLdesMemberId()),
				eq(member.getModel()), any(Observation.class));
		inOrder.verifyNoMoreInteractions();
	}

	@Test
	void when_MemberIsAddedToFirstFragment_TimebasedFragmentationIsAppliedAndRelationIsAdded() {
		Member member = mock(Member.class);
		when(member.getLdesMemberId()).thenReturn("memberId");
		when(openFragmentProvider.retrieveOpenFragmentOrCreateNewFragment(PARENT_FRAGMENT))
				.thenReturn(new ImmutablePair<>(OPEN_FRAGMENT, true));

		fragmentationStrategy.addMemberToFragment(PARENT_FRAGMENT,
				member.getLdesMemberId(), member.getModel(), any(Observation.class));

		InOrder inOrder = inOrder(openFragmentProvider, fragmentRepository,
				decoratedFragmentationStrategy);
		inOrder.verify(openFragmentProvider,
				times(1)).retrieveOpenFragmentOrCreateNewFragment(PARENT_FRAGMENT);
		inOrder.verify(fragmentRepository,
				times(1)).saveFragment(PARENT_FRAGMENT);
		inOrder.verify(decoratedFragmentationStrategy,
				times(1)).addMemberToFragment(eq(OPEN_FRAGMENT), eq(member.getLdesMemberId()),
				eq(member.getModel()), any(Observation.class));
		inOrder.verifyNoMoreInteractions();
	}
}
