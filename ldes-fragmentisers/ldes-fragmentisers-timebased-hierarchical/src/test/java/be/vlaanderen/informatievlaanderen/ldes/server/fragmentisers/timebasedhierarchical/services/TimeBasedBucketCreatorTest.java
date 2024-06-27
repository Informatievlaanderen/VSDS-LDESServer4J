package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebasedhierarchical.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.LdesFragmentIdentifier;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Bucket;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Fragment;
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
	private static final Fragment ROOT = new Fragment(new LdesFragmentIdentifier(VIEW_NAME, List.of()));
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
		when(bucketRepository.retrieveBucket(expectedBucketDescriptor.asDecodedString())).thenReturn(Optional.empty());

		Bucket child = bucketCreator.getOrCreateBucket(PARENT, TIME, Granularity.MONTH);

		assertThat(child.getBucketDescriptorAsString()).isEqualTo(expectedBucketDescriptor.asDecodedString());
		verify(bucketRepository,
				times(1)).retrieveBucket(expectedBucketDescriptor.asDecodedString());
//		verify(relationsAttributer, times(1)).addInBetweenRelation(PARENT, child);
//		verify(bucketRepository, times(1)).saveFragment(child);
//		verifyNoMoreInteractions(bucketRepository);

	}

	@Test
	void when_FragmentDoesNotExistAndIsDefaultFragment_Then_NewFragmentIsCreated() {
		BucketDescriptor expectedBucketDescriptor = new BucketDescriptor(
				List.of(new BucketDescriptorPair(Granularity.YEAR.getValue(), DEFAULT_BUCKET_STRING)));
		when(bucketRepository.retrieveBucket(expectedBucketDescriptor.asDecodedString())).thenReturn(Optional.empty());

		Bucket child = bucketCreator.getOrCreateBucket(new Bucket(BucketDescriptor.empty(), VIEW_NAME), DEFAULT_BUCKET_STRING, Granularity.YEAR);

		assertThat(child.getBucketDescriptorAsString()).isEqualTo(expectedBucketDescriptor.asDecodedString());
		verify(bucketRepository).retrieveBucket(expectedBucketDescriptor.asDecodedString());
//		verify(relationsAttributer, times(1)).addDefaultRelation(ROOT, child);/
//		verify(bucketRepository, times(1)).saveFragment(child);
//		verifyNoMoreInteractions(bucketRepository);

	}

	@Test
	void when_FragmentDoesExist_Then_FragmentIsRetrieved() {
		Bucket expectedChild = PARENT.createChild(new BucketDescriptorPair(Granularity.MONTH.getValue(), "01"));
		when(bucketRepository.retrieveBucket(expectedChild.getBucketDescriptorAsString())).thenReturn(Optional.of(expectedChild));

		Bucket child = bucketCreator.getOrCreateBucket(PARENT, TIME, Granularity.MONTH);

		assertThat(child.getBucketDescriptorAsString()).isEqualTo(expectedChild.getBucketDescriptorAsString());
//		verify(bucketRepository,
//				times(1)).retrieveFragment(expectedChild.getFragmentId());
//		verifyNoInteractions(relationsAttributer);
//		verifyNoMoreInteractions(bucketRepository);

	}
}