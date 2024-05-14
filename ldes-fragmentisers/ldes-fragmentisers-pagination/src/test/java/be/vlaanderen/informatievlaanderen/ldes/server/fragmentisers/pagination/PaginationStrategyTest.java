package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.pagination;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.FragmentPair;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.LdesFragmentIdentifier;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.FragmentationStrategy;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Fragment;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.FragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.pagination.services.OpenPageProvider;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

import java.util.List;

import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.pagination.constants.PaginationConstants.PAGE_NUMBER;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class PaginationStrategyTest {

	private static final ViewName VIEW_NAME = new ViewName("collectionName", "view");
	private final OpenPageProvider openPageProvider = mock(OpenPageProvider.class);
	private final FragmentationStrategy decoratedFragmentationStrategy = mock(FragmentationStrategy.class);
	private FragmentationStrategy fragmentationStrategy;
	private static Fragment PARENT_FRAGMENT;
	private static Fragment OPEN_FRAGMENT;
	private final FragmentRepository fragmentRepository = mock(FragmentRepository.class);

	@BeforeEach
	void setUp() {
		PARENT_FRAGMENT = new Fragment(new LdesFragmentIdentifier(VIEW_NAME, List.of()));
		OPEN_FRAGMENT = PARENT_FRAGMENT.createChild(new FragmentPair(PAGE_NUMBER, "1"));
		fragmentationStrategy = new PaginationStrategy(decoratedFragmentationStrategy,
				openPageProvider, ObservationRegistry.create(),
				fragmentRepository);
	}

	@Test
	void when_MemberIsAddedToFragment_PaginationIsApplied() {
		Member member = mock(Member.class);
		when(member.id()).thenReturn("memberId");
		when(openPageProvider.retrieveOpenFragmentOrCreateNewFragment(PARENT_FRAGMENT))
				.thenReturn(new ImmutablePair<>(OPEN_FRAGMENT, false));

		fragmentationStrategy.addMemberToFragment(PARENT_FRAGMENT,
				member, any(Observation.class));

		InOrder inOrder = inOrder(openPageProvider, fragmentRepository,
				decoratedFragmentationStrategy);
		inOrder.verify(openPageProvider,
				times(1)).retrieveOpenFragmentOrCreateNewFragment(PARENT_FRAGMENT);
		inOrder.verify(decoratedFragmentationStrategy,
				times(1))
				.addMemberToFragment(eq(OPEN_FRAGMENT), any(), any(Observation.class));
		inOrder.verifyNoMoreInteractions();
	}

	@Test
	void when_MemberIsAddedToFirstFragment_PaginationIsAppliedAndRelationIsAdded() {
		Member member = mock(Member.class);
		when(member.id()).thenReturn("memberId");
		when(openPageProvider.retrieveOpenFragmentOrCreateNewFragment(PARENT_FRAGMENT))
				.thenReturn(new ImmutablePair<>(OPEN_FRAGMENT, true));

		fragmentationStrategy.addMemberToFragment(PARENT_FRAGMENT,
				member, any(Observation.class));

		InOrder inOrder = inOrder(openPageProvider, fragmentRepository,
				decoratedFragmentationStrategy);
		inOrder.verify(openPageProvider,
				times(1)).retrieveOpenFragmentOrCreateNewFragment(PARENT_FRAGMENT);
		inOrder.verify(fragmentRepository,
				times(1)).saveFragment(PARENT_FRAGMENT);
		inOrder.verify(decoratedFragmentationStrategy,
				times(1))
				.addMemberToFragment(eq(OPEN_FRAGMENT), any(), any(Observation.class));

		inOrder.verifyNoMoreInteractions();
	}
}
