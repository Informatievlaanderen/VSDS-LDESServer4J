package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.substring;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentationStrategy;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.FragmentInfo;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.TreeRelation;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.valueobjects.FragmentPair;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.substring.bucketiser.SubstringBucketiser;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.substring.fragment.SubstringFragmentCreator;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.substring.fragment.SubstringFragmentFinder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.Tracer;

import java.util.List;

import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.substring.fragment.SubstringFragmentCreator.SUBSTRING;
import static org.mockito.Mockito.*;

class SubstringFragmentationStrategyTest {

	private static final String VIEW_NAME = "view";
	private static LdesFragment PARENT_FRAGMENT;
	private Tracer tracer;
	private SubstringBucketiser substringBucketiser;
	private SubstringFragmentFinder substringFragmentFinder;
	private SubstringFragmentCreator substringFragmentCreator;
	private SubstringFragmentationStrategy substringFragmentationStrategy;
	private final FragmentationStrategy decoratedFragmentationStrategy = mock(FragmentationStrategy.class);
	private final LdesFragmentRepository ldesFragmentRepository = mock(LdesFragmentRepository.class);

	@BeforeEach
	void setUp() {
		PARENT_FRAGMENT = new LdesFragment(
				new FragmentInfo(VIEW_NAME, List.of()));
		tracer = mock(Tracer.class);
		substringBucketiser = mock(SubstringBucketiser.class);
		substringFragmentFinder = mock(SubstringFragmentFinder.class);
		substringFragmentCreator = mock(SubstringFragmentCreator.class);
		substringFragmentationStrategy = new SubstringFragmentationStrategy(decoratedFragmentationStrategy,
				ldesFragmentRepository, tracer, substringBucketiser, substringFragmentFinder, substringFragmentCreator);
	}

	@Test
	void when_SubstringFragmentationStrategyIsCalled_SubstringFragmentationIsAppliedAndDecoratedServiceIsCalled() {
		Member member = mock(Member.class);
		Span parentSpan = mock(Span.class);
		Span childSpan = mock(Span.class);
		when(tracer.nextSpan(parentSpan)).thenReturn(childSpan);
		when(childSpan.name("substring fragmentation")).thenReturn(childSpan);
		when(childSpan.start()).thenReturn(childSpan);
		when(substringBucketiser.bucketise(member)).thenReturn(List.of("a", "ab", "abc"));
		LdesFragment rootFragment = PARENT_FRAGMENT.createChild(new FragmentPair(SUBSTRING, ""));
		when(substringFragmentCreator.getOrCreateSubstringFragment(PARENT_FRAGMENT, "")).thenReturn(rootFragment);
		LdesFragment childFragment = PARENT_FRAGMENT.createChild(new FragmentPair(SUBSTRING, "ab"));
		when(substringFragmentFinder.getOpenLdesFragmentOrLastPossibleFragment(PARENT_FRAGMENT, rootFragment,
				List.of("a", "ab", "abc"))).thenReturn(childFragment);

		substringFragmentationStrategy.addMemberToFragment(PARENT_FRAGMENT, member, parentSpan);

		InOrder inOrder = inOrder(ldesFragmentRepository, substringBucketiser, substringFragmentCreator,
				substringFragmentFinder, decoratedFragmentationStrategy);
		inOrder.verify(substringBucketiser, times(1)).bucketise(member);
		inOrder.verify(substringFragmentCreator, times(1)).getOrCreateSubstringFragment(PARENT_FRAGMENT, "");
		inOrder.verify(ldesFragmentRepository, times(1)).addRelationToFragment(eq(PARENT_FRAGMENT),
				any(TreeRelation.class));
		inOrder.verify(substringFragmentFinder, times(1)).getOpenLdesFragmentOrLastPossibleFragment(PARENT_FRAGMENT,
				rootFragment, List.of("a", "ab", "abc"));
		inOrder.verify(decoratedFragmentationStrategy, times(1)).addMemberToFragment(childFragment, member,
				childSpan);
		inOrder.verifyNoMoreInteractions();
	}
}