package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.substring;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentationStrategy;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.valueobjects.FragmentPair;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.relations.TreeRelationsRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.substring.fragment.SubstringFragmentCreator;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.substring.fragment.SubstringFragmentFinder;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.substring.model.LocalMember;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.substring.model.LocalMemberSupplier;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.substring.model.SubstringToken;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

import java.util.List;
import java.util.Set;

import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.substring.SubstringFragmentationStrategy.ROOT_SUBSTRING;
import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.substring.fragment.SubstringFragmentCreator.SUBSTRING;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

class SubstringFragmentationStrategyTest {

	private static final String VIEW_NAME = "view";
	private static LdesFragment PARENT_FRAGMENT;
	private LocalMemberSupplier localMemberSupplier;
	private SubstringFragmentFinder substringFragmentFinder;
	private SubstringFragmentCreator substringFragmentCreator;
	private SubstringFragmentationStrategy substringFragmentationStrategy;
	private final FragmentationStrategy decoratedFragmentationStrategy = mock(FragmentationStrategy.class);
	private final LdesFragmentRepository ldesFragmentRepository = mock(LdesFragmentRepository.class);
	private final TreeRelationsRepository treeRelationsRepository = mock(TreeRelationsRepository.class);

	@BeforeEach
	void setUp() {
		PARENT_FRAGMENT = new LdesFragment(
				VIEW_NAME, List.of());
		localMemberSupplier = mock(LocalMemberSupplier.class);
		substringFragmentFinder = mock(SubstringFragmentFinder.class);
		substringFragmentCreator = mock(SubstringFragmentCreator.class);
		substringFragmentationStrategy = new SubstringFragmentationStrategy(decoratedFragmentationStrategy,
				ObservationRegistry.create(), substringFragmentFinder,
				substringFragmentCreator, treeRelationsRepository, localMemberSupplier);
	}

	@Test
	void when_SubstringFragmentationStrategyIsCalled_SubstringFragmentationIsAppliedAndDecoratedServiceIsCalled() {
		Member member = mock(Member.class);
		LocalMember localMember = mock(LocalMember.class);
		when(localMemberSupplier.toLocalMember(member)).thenReturn(localMember);
		when(localMember.getTokens()).thenReturn(Set.of(new SubstringToken("abc")));
		LdesFragment rootFragment = PARENT_FRAGMENT.createChild(new FragmentPair(SUBSTRING, ""));
		when(substringFragmentCreator.getOrCreateSubstringFragment(PARENT_FRAGMENT,
				"")).thenReturn(rootFragment);
		LdesFragment childFragment = PARENT_FRAGMENT.createChild(new FragmentPair(SUBSTRING, "ab"));
		when(substringFragmentFinder.getOpenOrLastPossibleFragment(PARENT_FRAGMENT,
				rootFragment,
				List.of(ROOT_SUBSTRING, "a", "ab", "abc"))).thenReturn(childFragment);

		substringFragmentationStrategy.addMemberToFragment(PARENT_FRAGMENT, member,
				mock(Observation.class));

		InOrder inOrder = inOrder(ldesFragmentRepository,
				substringFragmentCreator,
				substringFragmentFinder, decoratedFragmentationStrategy);
		inOrder.verify(substringFragmentCreator,
				times(1)).getOrCreateSubstringFragment(PARENT_FRAGMENT, "");
		inOrder.verify(substringFragmentFinder,
				times(1)).getOpenOrLastPossibleFragment(PARENT_FRAGMENT,
						rootFragment, List.of(ROOT_SUBSTRING, "a", "ab", "abc"));
		inOrder.verify(decoratedFragmentationStrategy,
				times(1)).addMemberToFragment(eq(childFragment), eq(localMember),
						any(Observation.class));
		inOrder.verifyNoMoreInteractions();
	}
}