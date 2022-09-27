package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebased;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentationStrategy;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.FragmentInfo;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.TreeRelation;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.valueobjects.FragmentPair;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesmember.entities.LdesMember;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebased.services.OpenFragmentProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.Tracer;

import java.util.List;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.GENERIC_TREE_RELATION;
import static org.mockito.Mockito.*;

class TimebasedFragmentationStrategyTest {

	private static final String VIEW_NAME = "view";

	private final LdesFragmentRepository ldesFragmentRepository = mock(LdesFragmentRepository.class);
	private final OpenFragmentProvider openFragmentProvider = mock(OpenFragmentProvider.class);
	private final FragmentationStrategy wrappedService = mock(FragmentationStrategy.class);
	private final Tracer tracer = mock(Tracer.class);
	private FragmentationStrategy fragmentationStrategy;

	private static LdesFragment PARENT_FRAGMENT;
	private static LdesFragment OPEN_FRAGMENT;

	@BeforeEach
	void setUp() {
		PARENT_FRAGMENT = new LdesFragment("parentFragment",
				new FragmentInfo(VIEW_NAME, List.of()));
		OPEN_FRAGMENT = new LdesFragment("openFragment",
				new FragmentInfo(VIEW_NAME, List.of(new FragmentPair("generatedAtTime", "someTime"))));
		fragmentationStrategy = new TimebasedFragmentationStrategy(wrappedService,
				ldesFragmentRepository, openFragmentProvider, tracer);
	}

	@Test
	@DisplayName("Member Not Yet Added and No parent relation")
	void when_MemberisNotYetAddedAndNoParentRelation_thenFragmentationIsAppliedAndRelationCreated() {
		LdesMember ldesMember = mock(LdesMember.class);
		when(ldesMember.getLdesMemberId()).thenReturn("memberId");
		when(openFragmentProvider.retrieveOpenFragmentOrCreateNewFragment(PARENT_FRAGMENT.getFragmentInfo()))
				.thenReturn(OPEN_FRAGMENT);
		Span parentSpan = mock(Span.class);
		Span childSpan = mock(Span.class);
		when(tracer.nextSpan(parentSpan)).thenReturn(childSpan);
		when(childSpan.name("timebased fragmentation")).thenReturn(childSpan);
		when(childSpan.start()).thenReturn(childSpan);

		fragmentationStrategy.addMemberToFragment(PARENT_FRAGMENT,
				ldesMember, parentSpan);

		InOrder inOrder = inOrder(ldesFragmentRepository, openFragmentProvider, wrappedService);
		inOrder.verify(openFragmentProvider,
				times(1)).retrieveOpenFragmentOrCreateNewFragment(PARENT_FRAGMENT.getFragmentInfo());
		inOrder.verify(ldesFragmentRepository,
				times(1)).saveFragment(PARENT_FRAGMENT);
		inOrder.verify(wrappedService, times(1)).addMemberToFragment(OPEN_FRAGMENT, ldesMember, childSpan);
		inOrder.verifyNoMoreInteractions();
	}

	@Test
	@DisplayName("Member Not Yet Added but already parent relation")
	void when_MemberisNotYetAddedButAlreadyParentRelation_thenFragmentationIsApplied() {
		LdesMember ldesMember = mock(LdesMember.class);
		when(ldesMember.getLdesMemberId()).thenReturn("memberId");
		PARENT_FRAGMENT.addRelation(new TreeRelation("", OPEN_FRAGMENT.getFragmentId(), "", "",
				GENERIC_TREE_RELATION));
		when(openFragmentProvider.retrieveOpenFragmentOrCreateNewFragment(PARENT_FRAGMENT.getFragmentInfo()))
				.thenReturn(OPEN_FRAGMENT);
		Span parentSpan = mock(Span.class);
		Span childSpan = mock(Span.class);
		when(tracer.nextSpan(parentSpan)).thenReturn(childSpan);
		when(childSpan.name("timebased fragmentation")).thenReturn(childSpan);
		when(childSpan.start()).thenReturn(childSpan);

		fragmentationStrategy.addMemberToFragment(PARENT_FRAGMENT,
				ldesMember, parentSpan);

		InOrder inOrder = inOrder(ldesFragmentRepository, openFragmentProvider, wrappedService);
		inOrder.verify(openFragmentProvider,
				times(1)).retrieveOpenFragmentOrCreateNewFragment(PARENT_FRAGMENT.getFragmentInfo());
		inOrder.verify(wrappedService, times(1)).addMemberToFragment(OPEN_FRAGMENT, ldesMember, childSpan);
		inOrder.verifyNoMoreInteractions();
	}

	@Test
	@DisplayName("Member Already Added")
	void when_MemberIsAlreadyAdded_thenNoFragmentationIsApplied() {
		LdesMember ldesMember = mock(LdesMember.class);
		when(ldesMember.getLdesMemberId()).thenReturn("memberId");
		OPEN_FRAGMENT.addMember("memberId");
		when(openFragmentProvider.retrieveOpenFragmentOrCreateNewFragment(PARENT_FRAGMENT.getFragmentInfo()))
				.thenReturn(OPEN_FRAGMENT);
		Span parentSpan = mock(Span.class);
		Span childSpan = mock(Span.class);
		when(tracer.nextSpan(parentSpan)).thenReturn(childSpan);
		when(childSpan.name("timebased fragmentation")).thenReturn(childSpan);
		when(childSpan.start()).thenReturn(childSpan);

		fragmentationStrategy.addMemberToFragment(PARENT_FRAGMENT,
				ldesMember, parentSpan);

		InOrder inOrder = inOrder(ldesFragmentRepository, openFragmentProvider, wrappedService);
		inOrder.verify(openFragmentProvider,
				times(1)).retrieveOpenFragmentOrCreateNewFragment(PARENT_FRAGMENT.getFragmentInfo());
		inOrder.verifyNoMoreInteractions();
	}

}