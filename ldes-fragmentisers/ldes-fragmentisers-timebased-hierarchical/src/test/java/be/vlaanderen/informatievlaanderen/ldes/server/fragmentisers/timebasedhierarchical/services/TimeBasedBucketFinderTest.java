package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebasedhierarchical.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.FragmentPair;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Bucket;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.valueobjects.BucketDescriptor;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.valueobjects.BucketDescriptorPair;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebasedhierarchical.config.TimeBasedConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebasedhierarchical.constants.Granularity;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebasedhierarchical.model.FragmentationTimestamp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.ServerConstants.DEFAULT_BUCKET_STRING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class TimeBasedBucketFinderTest {
	private static final ViewName VIEW_NAME = new ViewName("collectionName", "view");
	private static final List<FragmentPair> timePairs = List.of(new FragmentPair(Granularity.YEAR.getValue(), "2023"),
			new FragmentPair(Granularity.MONTH.getValue(), "01"), new FragmentPair(Granularity.DAY.getValue(), "01"));
	private static final Bucket PARENT = new Bucket(BucketDescriptor.empty(), VIEW_NAME);
	private static final FragmentationTimestamp TIME = new FragmentationTimestamp(LocalDateTime.of(2023, 1, 1, 0, 0, 0),
			Granularity.DAY);
	private TimeBasedBucketCreator bucketCreator;
	private TimeBasedBucketFinder bucketFinder;

	@BeforeEach
	void setUp() {
		TimeBasedConfig config = new TimeBasedConfig(".*", "", Granularity.DAY, false);
		bucketCreator = mock(TimeBasedBucketCreator.class);
		bucketFinder = new TimeBasedBucketFinder(bucketCreator, config);

	}

	@Test
	void when_GetLowestIsCalled_Then_ReturnExpectedBucket() {
		final BucketDescriptor expectedBucketDescriptor = BucketDescriptor.of(
				new BucketDescriptorPair(Granularity.YEAR.getValue(), "2023"),
				new BucketDescriptorPair(Granularity.MONTH.getValue(), "01"),
				new BucketDescriptorPair(Granularity.DAY.getValue(), "01")
		);
		Bucket expected = new Bucket(expectedBucketDescriptor, VIEW_NAME);
		Bucket yearBucket = PARENT.createChild(new BucketDescriptorPair(Granularity.YEAR.getValue(), "2023"));
		Bucket monthBucket = yearBucket.createChild(new BucketDescriptorPair(Granularity.MONTH.getValue(), "01"));
		Bucket dayBucket = monthBucket.createChild(new BucketDescriptorPair(Granularity.DAY.getValue(), "01"));
		when(bucketCreator.getOrCreateBucket(PARENT, TIME, Granularity.YEAR)).thenReturn(yearBucket);
		when(bucketCreator.getOrCreateBucket(yearBucket, TIME, Granularity.MONTH)).thenReturn(monthBucket);
		when(bucketCreator.getOrCreateBucket(monthBucket, TIME, Granularity.DAY)).thenReturn(dayBucket);

		Bucket actual = bucketFinder.getLowestBucket(PARENT, TIME, Granularity.YEAR);

		assertThat(actual).isEqualTo(expected);
	}

	@Test
	void when_GetDefaultIsCalled_Then_ReturnExpectedFragment() {
		bucketFinder.getDefaultFragment(PARENT);

		verify(bucketCreator).getOrCreateBucket(PARENT, DEFAULT_BUCKET_STRING, Granularity.YEAR);
	}


}