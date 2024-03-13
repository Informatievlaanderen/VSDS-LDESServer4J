package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebasedhierarchical.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.FragmentPair;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.LdesFragmentIdentifier;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Fragment;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebasedhierarchical.config.TimeBasedConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebasedhierarchical.constants.Granularity;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebasedhierarchical.model.FragmentationTimestamp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.ServerConstants.DEFAULT_BUCKET_STRING;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class TimeBasedFragmentFinderTest {

	private static final ViewName VIEW_NAME = new ViewName("collectionName", "view");
	private static final List<FragmentPair> timePairs = List.of(new FragmentPair(Granularity.YEAR.getValue(), "2023"),
			new FragmentPair(Granularity.MONTH.getValue(), "01"), new FragmentPair(Granularity.DAY.getValue(), "01"));
	private static final Fragment PARENT = new Fragment(new LdesFragmentIdentifier(VIEW_NAME, List.of()));
	private static final FragmentationTimestamp TIME = new FragmentationTimestamp(LocalDateTime.of(2023, 1, 1, 0, 0, 0),
			Granularity.DAY);
	private TimeBasedConfig config;
	private TimeBasedFragmentCreator fragmentCreator;
	private TimeBasedFragmentFinder fragmentFinder;

	@BeforeEach
	void setUp() {
		config = new TimeBasedConfig(".*", "", Granularity.DAY, false);
		fragmentCreator = mock(TimeBasedFragmentCreator.class);
		fragmentFinder = new TimeBasedFragmentFinder(fragmentCreator, config);

	}

	@Test
	void when_GetLowestIsCalled_Then_ReturnExpectedFragment() {
		Fragment expected = new Fragment(new LdesFragmentIdentifier(VIEW_NAME, timePairs));
		Fragment yearFragment = PARENT.createChild(new FragmentPair(Granularity.YEAR.getValue(), "2023"));
		Fragment monthFragment = yearFragment.createChild(new FragmentPair(Granularity.MONTH.getValue(), "01"));
		Fragment dayFragment = monthFragment.createChild(new FragmentPair(Granularity.DAY.getValue(), "01"));
		when(fragmentCreator.getOrCreateFragment(PARENT, TIME, Granularity.YEAR)).thenReturn(yearFragment);
		when(fragmentCreator.getOrCreateFragment(yearFragment, TIME, Granularity.MONTH)).thenReturn(monthFragment);
		when(fragmentCreator.getOrCreateFragment(monthFragment, TIME, Granularity.DAY)).thenReturn(dayFragment);

		Fragment actual = fragmentFinder.getLowestFragment(PARENT, TIME, Granularity.YEAR);

		assertEquals(expected, actual);
	}

	@Test
	void when_GetDefaultIsCalled_Then_ReturnExpectedFragment() {
		Fragment actual = fragmentFinder.getDefaultFragment(PARENT);

		verify(fragmentCreator, times(1)).getOrCreateFragment(PARENT, DEFAULT_BUCKET_STRING, Granularity.YEAR);
	}
}
