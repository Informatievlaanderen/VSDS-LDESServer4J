package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebasedhierarchical.services;

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

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.ServerConstants.DEFAULT_BUCKET_STRING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class TimeBasedBucketFinderTest {
	private static final ViewName VIEW_NAME = new ViewName("collectionName", "view");
	private static final Bucket PARENT = new Bucket(BucketDescriptor.empty(), VIEW_NAME);
	private static final FragmentationTimestamp TIME = new FragmentationTimestamp(LocalDateTime.of(2023, 1, 1, 0, 0, 0),
			Granularity.DAY);
	private TimeBasedBucketCreator bucketCreator;
	private TimeBasedBucketFinder bucketFinder;

	@BeforeEach
	void setUp() {
		TimeBasedConfig config = new TimeBasedConfig(".*", "", Granularity.DAY);
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
		when(bucketCreator.createBucket(PARENT, TIME, Granularity.YEAR)).thenReturn(yearBucket);
		when(bucketCreator.createBucket(yearBucket, TIME, Granularity.MONTH)).thenReturn(monthBucket);
		when(bucketCreator.createBucket(monthBucket, TIME, Granularity.DAY)).thenReturn(dayBucket);

		Bucket actual = bucketFinder.getLowestBucket(PARENT, TIME, Granularity.YEAR);

		assertThat(actual).isEqualTo(expected);
	}

	@Test
	void when_GetDefaultIsCalled_Then_ReturnExpectedFragment() {
		bucketFinder.getDefaultFragment(PARENT);

		verify(bucketCreator).createBucket(PARENT, DEFAULT_BUCKET_STRING, Granularity.YEAR);
	}


}