// package
// be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebased;
//
// import
// be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
// import
// be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentationStrategy;
// import
// be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.FragmentInfo;
// import
// be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.valueobjects.FragmentPair;
// import
// be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.entities.Member;
// import
// be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.relations.TreeRelationsRepository;
// import
// be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebased.services.OpenFragmentProvider;
// import org.apache.commons.lang3.tuple.ImmutablePair;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.mockito.InOrder;
// import org.springframework.cloud.sleuth.Span;
// import org.springframework.cloud.sleuth.Tracer;
//
// import java.util.List;
//
// import static org.mockito.Mockito.*;
//
// class TimebasedFragmentationStrategyTest {
//
// private static final String VIEW_NAME = "view";
// private final OpenFragmentProvider openFragmentProvider =
// mock(OpenFragmentProvider.class);
// private final FragmentationStrategy decoratedFragmentationStrategy =
// mock(FragmentationStrategy.class);
//
// private final ObservationRegistry tracer = mock(Tracer.class);
//
// private FragmentationStrategy fragmentationStrategy;
// private static LdesFragment PARENT_FRAGMENT;
// private static LdesFragment OPEN_FRAGMENT;
// private final TreeRelationsRepository treeRelationsRepository =
// mock(TreeRelationsRepository.class);
//
// @BeforeEach
// void setUp() {
// PARENT_FRAGMENT = new LdesFragment(new FragmentInfo(VIEW_NAME, List.of()));
// OPEN_FRAGMENT = PARENT_FRAGMENT.createChild(new
// FragmentPair("generatedAtTime", "someTime"));
// fragmentationStrategy = new
// TimebasedFragmentationStrategy(decoratedFragmentationStrategy,
// openFragmentProvider, tracer,
// treeRelationsRepository);
// }
//
// @Test
// void when_MemberIsAddedToFragment_TimebasedFragmentationIsApplied() {
// Member member = mock(Member.class);
// when(member.getLdesMemberId()).thenReturn("memberId");
// when(openFragmentProvider.retrieveOpenFragmentOrCreateNewFragment(PARENT_FRAGMENT))
// .thenReturn(new ImmutablePair<>(OPEN_FRAGMENT, false));
// Observation parentObservation = mock(Span.class);
// Observation childObservation = mock(Span.class);
// when(tracer.nextSpan(parentSpan)).thenReturn(childSpan);
// when(childSpan.name("timebased fragmentation")).thenReturn(childSpan);
// when(childSpan.start()).thenReturn(childSpan);
//
// fragmentationStrategy.addMemberToFragment(PARENT_FRAGMENT,
// member, parentSpan);
//
// InOrder inOrder = inOrder(openFragmentProvider, treeRelationsRepository,
// decoratedFragmentationStrategy);
// inOrder.verify(openFragmentProvider,
// times(1)).retrieveOpenFragmentOrCreateNewFragment(PARENT_FRAGMENT);
// inOrder.verify(decoratedFragmentationStrategy,
// times(1)).addMemberToFragment(OPEN_FRAGMENT, member,
// childSpan);
// inOrder.verifyNoMoreInteractions();
// }
//
// @Test
// void
// when_MemberIsAddedToFirstFragment_TimebasedFragmentationIsAppliedAndRelationIsAdded()
// {
// Member member = mock(Member.class);
// when(member.getLdesMemberId()).thenReturn("memberId");
// when(openFragmentProvider.retrieveOpenFragmentOrCreateNewFragment(PARENT_FRAGMENT))
// .thenReturn(new ImmutablePair<>(OPEN_FRAGMENT, true));
// Observation parentObservation = mock(Span.class);
// Observation childObservation = mock(Span.class);
// when(tracer.nextSpan(parentSpan)).thenReturn(childSpan);
// when(childSpan.name("timebased fragmentation")).thenReturn(childSpan);
// when(childSpan.start()).thenReturn(childSpan);
//
// fragmentationStrategy.addMemberToFragment(PARENT_FRAGMENT,
// member, parentSpan);
//
// InOrder inOrder = inOrder(openFragmentProvider, treeRelationsRepository,
// decoratedFragmentationStrategy);
// inOrder.verify(openFragmentProvider,
// times(1)).retrieveOpenFragmentOrCreateNewFragment(PARENT_FRAGMENT);
// inOrder.verify(treeRelationsRepository,
// times(1)).addTreeRelation(eq(PARENT_FRAGMENT.getFragmentId()), any());
// inOrder.verify(decoratedFragmentationStrategy,
// times(1)).addMemberToFragment(OPEN_FRAGMENT, member,
// childSpan);
// inOrder.verifyNoMoreInteractions();
// }
// }