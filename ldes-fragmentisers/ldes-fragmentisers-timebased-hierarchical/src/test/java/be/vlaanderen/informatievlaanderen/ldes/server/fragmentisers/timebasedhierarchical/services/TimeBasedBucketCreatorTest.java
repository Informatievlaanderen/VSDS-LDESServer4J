package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebasedhierarchical.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Bucket;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.BucketRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.valueobjects.BucketDescriptor;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.valueobjects.BucketDescriptorPair;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebasedhierarchical.constants.Granularity;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebasedhierarchical.model.FragmentationTimestamp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.ServerConstants.DEFAULT_BUCKET_STRING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class TimeBasedBucketCreatorTest {
	private static final ViewName VIEW_NAME = new ViewName("collectionName", "view");
	private static final BucketDescriptorPair timePair = new BucketDescriptorPair(Granularity.YEAR.getValue(), "2023");
	private static final Bucket PARENT = new Bucket(new BucketDescriptor(List.of(timePair)), VIEW_NAME);
	private static final Bucket ROOT = new Bucket(BucketDescriptor.empty(), VIEW_NAME);
	private static final FragmentationTimestamp TIME = new FragmentationTimestamp(LocalDateTime.of(2023, 1, 1, 0, 0, 0),
			Granularity.MONTH);

	private BucketRepository bucketRepository;
	private TimeBasedRelationsAttributer relationsAttributer;
	private TimeBasedBucketCreator bucketCreator;

	@BeforeEach
	void setUp() {
		bucketRepository = mock(BucketRepository.class);
		relationsAttributer = mock(TimeBasedRelationsAttributer.class);
		bucketCreator = new TimeBasedBucketCreator(bucketRepository, relationsAttributer);
	}

	@Test
	void when_FragmentDoesNotExist_Then_NewFragmentIsCreated() {
		final BucketDescriptor expectedBucketDescriptor = new BucketDescriptor(List.of(timePair, new BucketDescriptorPair(Granularity.MONTH.getValue(), "01")));
		when(bucketRepository.retrieveBucket(VIEW_NAME, expectedBucketDescriptor)).thenReturn(Optional.empty());

		Bucket child = bucketCreator.getOrCreateBucket(PARENT, TIME, Granularity.MONTH);

		assertThat(child.getBucketDescriptor()).isEqualTo(expectedBucketDescriptor);
		verify(bucketRepository,
				times(1)).retrieveBucket(VIEW_NAME, expectedBucketDescriptor);
		verify(relationsAttributer).addInBetweenRelation(PARENT, child);
		verify(bucketRepository).insertBucket(child);
		verifyNoMoreInteractions(bucketRepository);

	}

	@Test
	void when_FragmentDoesNotExistAndIsDefaultFragment_Then_NewFragmentIsCreated() {
		BucketDescriptor expectedBucketDescriptor = new BucketDescriptor(
				List.of(new BucketDescriptorPair(Granularity.YEAR.getValue(), DEFAULT_BUCKET_STRING)));
		when(bucketRepository.retrieveBucket(VIEW_NAME, expectedBucketDescriptor)).thenReturn(Optional.empty());

		Bucket child = bucketCreator.getOrCreateBucket(new Bucket(BucketDescriptor.empty(), VIEW_NAME), DEFAULT_BUCKET_STRING, Granularity.YEAR);

		assertThat(child.getBucketDescriptor()).isEqualTo(expectedBucketDescriptor);
		verify(bucketRepository).retrieveBucket(VIEW_NAME, expectedBucketDescriptor);
		verify(relationsAttributer).addDefaultRelation(ROOT, child);
		verify(bucketRepository).insertBucket(child);
		verifyNoMoreInteractions(bucketRepository);
	}

	@Test
	void when_FragmentDoesExist_Then_FragmentIsRetrieved() {
		Bucket expectedChild = PARENT.createChild(new BucketDescriptorPair(Granularity.MONTH.getValue(), "01"));
		when(bucketRepository.retrieveBucket(VIEW_NAME, expectedChild.getBucketDescriptor())).thenReturn(Optional.of(expectedChild));

		Bucket child = bucketCreator.getOrCreateBucket(PARENT, TIME, Granularity.MONTH);

		assertThat(child.getBucketDescriptorAsString()).isEqualTo(expectedChild.getBucketDescriptorAsString());
		verify(bucketRepository).retrieveBucket(VIEW_NAME, expectedChild.getBucketDescriptor());
		verifyNoInteractions(relationsAttributer);
		verifyNoMoreInteractions(bucketRepository);
	}
}