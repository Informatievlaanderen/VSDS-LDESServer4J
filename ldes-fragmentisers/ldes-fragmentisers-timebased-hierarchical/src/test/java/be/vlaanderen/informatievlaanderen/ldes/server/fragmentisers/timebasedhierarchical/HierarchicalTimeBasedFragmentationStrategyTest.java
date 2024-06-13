package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebasedhierarchical;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.FragmentPair;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.LdesFragmentIdentifier;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.FragmentationStrategy;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Fragment;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.FragmentationMember;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.FragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebasedhierarchical.config.TimeBasedConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebasedhierarchical.constants.Granularity;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebasedhierarchical.model.FragmentationTimestamp;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebasedhierarchical.services.TimeBasedFragmentFinder;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import org.apache.jena.rdf.model.Model;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.List;

import static org.apache.jena.riot.RDFDataMgr.loadModel;
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
	private FragmentationStrategy decoratedFragmentationStrategy;

	@BeforeEach
	void setUp() {
		PARENT_FRAGMENT = new Fragment(new LdesFragmentIdentifier(VIEW_NAME, List.of()));
		CHILD_FRAGMENT = new Fragment(new LdesFragmentIdentifier(VIEW_NAME, List.of(new FragmentPair("is", "child"))));
		TIME = LocalDateTime.of(2023, 1, 1, 0, 0, 0);
		GRANULARITY = Granularity.SECOND;
		TimeBasedConfig config = new TimeBasedConfig(".*", "http://purl.org/dc/terms/created", GRANULARITY, false);
		fragmentFinder = mock(TimeBasedFragmentFinder.class);
		decoratedFragmentationStrategy = mock(FragmentationStrategy.class);
		FragmentRepository fragmentRepository = mock(FragmentRepository.class);
		fragmentationStrategy = new HierarchicalTimeBasedFragmentationStrategy(decoratedFragmentationStrategy,
				ObservationRegistry.create(), fragmentFinder, fragmentRepository, config);
	}

	@Test
	void when_FragmentationCalled_Then_FunctionsAreCalled() {
		Model model = loadModel("member_with_created_property.nq");
		FragmentationMember member = new FragmentationMember("1", model, 1L);
		FragmentationTimestamp fragmentationTimestamp = new FragmentationTimestamp(TIME, GRANULARITY);
		when(fragmentFinder.getLowestFragment(PARENT_FRAGMENT, fragmentationTimestamp, Granularity.YEAR))
				.thenReturn(CHILD_FRAGMENT);

		fragmentationStrategy.addMemberToFragment(PARENT_FRAGMENT, member,
				mock(Observation.class));

		InOrder inOrder = Mockito.inOrder(fragmentFinder, decoratedFragmentationStrategy);
		inOrder.verify(fragmentFinder).getLowestFragment(PARENT_FRAGMENT, fragmentationTimestamp, Granularity.YEAR);
		inOrder.verify(decoratedFragmentationStrategy,
				times(1)).addMemberToFragment(eq(CHILD_FRAGMENT), any(),
						any(Observation.class));
	}

	@Test
	void when_FragmentationCalledForMemberWithMissingTimestamp_Then_FunctionsAreCalled() {
		FragmentationMember member = mock(FragmentationMember.class);
		when(fragmentFinder.getDefaultFragment(PARENT_FRAGMENT))
				.thenReturn(CHILD_FRAGMENT);

		fragmentationStrategy.addMemberToFragment(PARENT_FRAGMENT, member,
				mock(Observation.class));

		InOrder inOrder = Mockito.inOrder(fragmentFinder, decoratedFragmentationStrategy);
		inOrder.verify(fragmentFinder).getDefaultFragment(PARENT_FRAGMENT);
		inOrder.verify(decoratedFragmentationStrategy,
				times(1)).addMemberToFragment(eq(CHILD_FRAGMENT), any(),
				any(Observation.class));
	}

}
