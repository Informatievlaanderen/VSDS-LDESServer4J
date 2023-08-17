package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.substring;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ModelParser;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.LdesFragmentIdentifier;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.valueobjects.FragmentPair;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.FragmentationStrategy;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Fragment;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.FragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.substring.config.SubstringConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.substring.fragment.SubstringFragmentCreator;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.substring.fragment.SubstringFragmentFinder;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;

import java.util.List;

import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.substring.SubstringFragmentationStrategy.ROOT_SUBSTRING;
import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.substring.fragment.SubstringFragmentCreator.SUBSTRING;
import static org.mockito.Mockito.*;

class SubstringFragmentationStrategyTest {

	private static final ViewName VIEW_NAME = new ViewName("collectionName", "view");
	private static Fragment PARENT_FRAGMENT;
	private SubstringFragmentFinder substringFragmentFinder;
	private SubstringFragmentCreator substringFragmentCreator;
	private SubstringFragmentationStrategy substringFragmentationStrategy;
	private SubstringConfig substringConfig;
	private final FragmentationStrategy decoratedFragmentationStrategy = mock(FragmentationStrategy.class);
	private final FragmentRepository fragmentRepository = mock(FragmentRepository.class);

	@BeforeEach
	void setUp() {
		PARENT_FRAGMENT = new Fragment(new LdesFragmentIdentifier(VIEW_NAME, List.of()));
		substringFragmentFinder = mock(SubstringFragmentFinder.class);
		substringFragmentCreator = mock(SubstringFragmentCreator.class);
		substringConfig = new SubstringConfig();
		substringFragmentationStrategy = new SubstringFragmentationStrategy(decoratedFragmentationStrategy,
				ObservationRegistry.create(), substringFragmentFinder,
				substringFragmentCreator, fragmentRepository, substringConfig);
	}

	@Test
	void when_SubstringFragmentationStrategyIsCalled_SubstringFragmentationIsAppliedAndDecoratedServiceIsCalled() {
		Member member = mock(Member.class);
		var modelParserMock = Mockito.mockStatic(ModelParser.class);
		modelParserMock.when(() -> ModelParser.getFragmentationObject(eq(member.model()), any(), any()))
				.thenReturn("abc");
		Fragment rootFragment = PARENT_FRAGMENT.createChild(new FragmentPair(SUBSTRING, ""));
		when(substringFragmentCreator.getOrCreateSubstringFragment(PARENT_FRAGMENT,
				"")).thenReturn(rootFragment);
		Fragment childFragment = PARENT_FRAGMENT.createChild(new FragmentPair(SUBSTRING, "ab"));
		when(substringFragmentFinder.getOpenOrLastPossibleFragment(PARENT_FRAGMENT,
				rootFragment,
				List.of(ROOT_SUBSTRING, "a", "ab", "abc"))).thenReturn(childFragment);

		substringFragmentationStrategy.addMemberToFragment(PARENT_FRAGMENT, member.id(), member.model(),
				mock(Observation.class));

		InOrder inOrder = inOrder(fragmentRepository,
				substringFragmentCreator,
				substringFragmentFinder, decoratedFragmentationStrategy);
		inOrder.verify(substringFragmentCreator,
				times(1)).getOrCreateSubstringFragment(PARENT_FRAGMENT, "");
		inOrder.verify(substringFragmentFinder,
				times(1)).getOpenOrLastPossibleFragment(PARENT_FRAGMENT,
						rootFragment, List.of(ROOT_SUBSTRING, "a", "ab", "abc"));
		inOrder.verify(decoratedFragmentationStrategy,
				times(1)).addMemberToFragment(eq(childFragment), any(),
						any(), any(Observation.class));
		inOrder.verifyNoMoreInteractions();
	}
}
