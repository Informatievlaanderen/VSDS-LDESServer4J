package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebasedhierarchical;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ModelParser;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.LdesFragmentIdentifier;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.valueobjects.FragmentPair;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.FragmentationStrategy;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Fragment;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.FragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebasedhierarchical.config.TimeBasedConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebasedhierarchical.constants.Granularity;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebasedhierarchical.model.FragmentationTimestamp;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebasedhierarchical.services.TimeBasedFragmentFinder;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class HierarchicalTimeBasedFragmentationStrategyTest {

	private static final ViewName VIEW_NAME = new ViewName("collectionName", "view");
	private static Fragment PARENT_FRAGMENT;
	private static Fragment CHILD_FRAGMENT;
	private static LocalDateTime TIME;
	private static Granularity GRANULARITY;
	private HierarchicalTimeBasedFragmentationStrategy fragmentationStrategy;
	private TimeBasedFragmentFinder fragmentFinder;
	private TimeBasedConfig config;
	private FragmentationStrategy decoratedFragmentationStrategy;
	private FragmentRepository fragmentRepository;

	@BeforeEach
	void setUp() {
		PARENT_FRAGMENT = new Fragment(new LdesFragmentIdentifier(VIEW_NAME, List.of()));
		CHILD_FRAGMENT = new Fragment(new LdesFragmentIdentifier(VIEW_NAME, List.of(new FragmentPair("is", "child"))));
		TIME = LocalDateTime.of(2023, 1, 1, 0, 0, 0);
		GRANULARITY = Granularity.SECOND;
		config = new TimeBasedConfig(".*", "", GRANULARITY);
		fragmentFinder = mock(TimeBasedFragmentFinder.class);
		decoratedFragmentationStrategy = mock(FragmentationStrategy.class);
		fragmentRepository = mock(FragmentRepository.class);
		fragmentationStrategy = new HierarchicalTimeBasedFragmentationStrategy(decoratedFragmentationStrategy,
				ObservationRegistry.create(), fragmentFinder, fragmentRepository, config);
	}

	@Test
	void when_FragmentationCalled_Then_FunctionsAreCalled() {
		Member member = mock(Member.class);
		FragmentationTimestamp fragmentationTimestamp = new FragmentationTimestamp(TIME, GRANULARITY);
		var modelParserMock = Mockito.mockStatic(ModelParser.class);
		modelParserMock.when(() -> ModelParser.getFragmentationObjectLocalDateTime(eq(member.model()), any(), any()))
				.thenReturn(TIME);
		when(fragmentFinder.getLowestFragment(PARENT_FRAGMENT, fragmentationTimestamp, Granularity.YEAR))
				.thenReturn(CHILD_FRAGMENT);

		fragmentationStrategy.addMemberToFragment(PARENT_FRAGMENT, member.id(), member.model(),
				mock(Observation.class));

		InOrder inOrder = Mockito.inOrder(fragmentFinder, decoratedFragmentationStrategy);
		inOrder.verify(fragmentFinder).getLowestFragment(PARENT_FRAGMENT, fragmentationTimestamp, Granularity.YEAR);
		inOrder.verify(decoratedFragmentationStrategy,
				times(1)).addMemberToFragment(eq(CHILD_FRAGMENT), any(),
						any(), any(Observation.class));
	}

}
