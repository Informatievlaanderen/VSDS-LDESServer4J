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
	private static Fragment parentFragment;
	private static Fragment childFragment;
	private static LocalDateTime time;
	private static Granularity granularity;
	private HierarchicalTimeBasedFragmentationStrategy fragmentationStrategy;
	private TimeBasedFragmentFinder fragmentFinder;
	private FragmentationStrategy decoratedFragmentationStrategy;

	@BeforeEach
	void setUp() {
		parentFragment = new Fragment(new LdesFragmentIdentifier(VIEW_NAME, List.of()));
		childFragment = new Fragment(new LdesFragmentIdentifier(VIEW_NAME, List.of(new FragmentPair("is", "child"))));
		time = LocalDateTime.of(2023, 1, 1, 0, 0, 0);
		granularity = Granularity.SECOND;
		TimeBasedConfig config = new TimeBasedConfig(".*", "http://purl.org/dc/terms/created", granularity, false);
		fragmentFinder = mock(TimeBasedFragmentFinder.class);
		decoratedFragmentationStrategy = mock(FragmentationStrategy.class);
		FragmentRepository fragmentRepository = mock(FragmentRepository.class);
		fragmentationStrategy = new HierarchicalTimeBasedFragmentationStrategy(decoratedFragmentationStrategy,
				ObservationRegistry.create(), fragmentFinder, fragmentRepository, config);
	}

	@Test
	void when_FragmentationCalled_Then_FunctionsAreCalled() {
		Model model = loadModel("member_with_created_property.nq");
		FragmentationMember member = new FragmentationMember("1", model);
		FragmentationTimestamp fragmentationTimestamp = new FragmentationTimestamp(time, granularity);
		when(fragmentFinder.getLowestFragment(parentFragment, fragmentationTimestamp, Granularity.YEAR))
				.thenReturn(childFragment);

		fragmentationStrategy.addMemberToFragment(parentFragment, member,
				mock(Observation.class));

		InOrder inOrder = Mockito.inOrder(fragmentFinder, decoratedFragmentationStrategy);
		inOrder.verify(fragmentFinder).getLowestFragment(parentFragment, fragmentationTimestamp, Granularity.YEAR);
		inOrder.verify(decoratedFragmentationStrategy,
				times(1)).addMemberToFragment(eq(childFragment), any(),
						any(Observation.class));
	}

	@Test
	void when_FragmentationCalledForMemberWithMissingTimestamp_Then_FunctionsAreCalled() {
		FragmentationMember member = mock(FragmentationMember.class);
		when(fragmentFinder.getDefaultFragment(parentFragment))
				.thenReturn(childFragment);

		fragmentationStrategy.addMemberToFragment(parentFragment, member,
				mock(Observation.class));

		InOrder inOrder = Mockito.inOrder(fragmentFinder, decoratedFragmentationStrategy);
		inOrder.verify(fragmentFinder).getDefaultFragment(parentFragment);
		inOrder.verify(decoratedFragmentationStrategy,
				times(1)).addMemberToFragment(eq(childFragment), any(),
				any(Observation.class));
	}

}
