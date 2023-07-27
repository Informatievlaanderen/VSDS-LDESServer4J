package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebasedhierarchical.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.LdesFragmentIdentifier;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.valueobjects.FragmentPair;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Fragment;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebasedhierarchical.config.TimeBasedConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebasedhierarchical.model.FragmentationTimestamp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TimeBasedFragmentFinderTest {

	private static final ViewName VIEW_NAME = new ViewName("collectionName", "view");
	private static final List<FragmentPair> timePairs = List.of(new FragmentPair("Y", "2023"),
			new FragmentPair("M", "1"), new FragmentPair("D", "1"));
	private static final Fragment PARENT = new Fragment(new LdesFragmentIdentifier(VIEW_NAME, List.of()));
	private static final FragmentationTimestamp TIME = new FragmentationTimestamp(LocalDateTime.of(2023, 1, 1, 0, 0, 0),
			"D");
	private TimeBasedConfig config;
	private TimeBasedFragmentCreator fragmentCreator;
	private TimeBasedFragmentFinder fragmentFinder;

	@BeforeEach
	void setUp() {
		config = new TimeBasedConfig(".*", "", "D");
		fragmentCreator = mock(TimeBasedFragmentCreator.class);
		fragmentFinder = new TimeBasedFragmentFinder(fragmentCreator, config);

	}

	@Test
	void when_GetLowestIsCalled_Then_ReturnExpectedFragment() {
		Fragment expected = new Fragment(new LdesFragmentIdentifier(VIEW_NAME, timePairs));
		Fragment firstSub = PARENT.createChild(new FragmentPair("Y", "2023"));
		Fragment secondSub = firstSub.createChild(new FragmentPair("M", "1"));
		Fragment thirdSub = secondSub.createChild(new FragmentPair("D", "1"));
		when(fragmentCreator.getOrCreateFragment(PARENT, TIME, 0)).thenReturn(firstSub);
		when(fragmentCreator.getOrCreateFragment(firstSub, TIME, 1)).thenReturn(secondSub);
		when(fragmentCreator.getOrCreateFragment(secondSub, TIME, 2)).thenReturn(thirdSub);

		Fragment actual = fragmentFinder.getLowestFragment(PARENT, TIME, 0);

		assertEquals(expected, actual);
	}
}