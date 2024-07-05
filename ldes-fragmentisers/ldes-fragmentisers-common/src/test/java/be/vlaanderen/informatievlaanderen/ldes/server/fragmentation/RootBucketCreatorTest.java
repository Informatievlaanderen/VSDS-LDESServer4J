package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Bucket;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.BucketRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.valueobjects.BucketDescriptor;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RootBucketCreatorTest {

	@Mock
	private BucketRepository bucketRepository;

	private Observation observation;
	private RootBucketCreator rootBucketCreator;
	private Bucket rootBucket;

	private static final ObservationRegistry observationRegistry = ObservationRegistry.NOOP;
	private static final ViewName VIEW_NAME = new ViewName("collection", "view");

	@BeforeEach
	void setUp() {
		observation = Observation.createNotStarted("observation", observationRegistry).start();

		rootBucketCreator = new RootBucketCreator(bucketRepository, observationRegistry);
		rootBucket = new Bucket(BucketDescriptor.empty(), VIEW_NAME);
		when(bucketRepository.retrieveRootBucket(VIEW_NAME)).thenReturn(Optional.of(rootBucket));
	}

	@Test
	void should_FetchRootFragment_when_NotYetInMemory() {
		final Bucket result = rootBucketCreator.getOrCreateRootBucket(VIEW_NAME, observation);

		assertEquals(rootBucket, result);
		verify(bucketRepository).retrieveRootBucket(VIEW_NAME);
	}

	@Test
	void should_ReturnFragmentFromMemory_when_FetchedEarlier() {
		rootBucketCreator.getOrCreateRootBucket(VIEW_NAME, observation);
		rootBucketCreator.getOrCreateRootBucket(VIEW_NAME, observation);
		rootBucketCreator.getOrCreateRootBucket(VIEW_NAME, observation);
		final Bucket result = rootBucketCreator.getOrCreateRootBucket(VIEW_NAME, observation);

		assertEquals(rootBucket, result);
		verify(bucketRepository).retrieveRootBucket(VIEW_NAME);
	}

	@Test
	void insertNewRootBucket_when_RootBucketDoesNotYetExist() {
		when(bucketRepository.retrieveRootBucket(VIEW_NAME)).thenReturn(Optional.empty());

		rootBucketCreator.getOrCreateRootBucket(VIEW_NAME, observation);

		verify(bucketRepository).retrieveRootBucket(VIEW_NAME);
		verify(bucketRepository).insertBucket(any());
	}
}
