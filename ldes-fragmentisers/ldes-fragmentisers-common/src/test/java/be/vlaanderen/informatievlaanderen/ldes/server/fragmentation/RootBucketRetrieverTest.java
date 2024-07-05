package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Bucket;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.exceptions.MissingRootFragmentException;
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

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RootBucketRetrieverTest {

	@Mock
	private BucketRepository bucketRepository;

	private Observation observation;
	private RootBucketRetriever rootBucketRetriever;
	private Bucket rootBucket;

	private static final ObservationRegistry observationRegistry = ObservationRegistry.NOOP;
	private static final ViewName VIEW_NAME = new ViewName("collection", "view");

	@BeforeEach
	void setUp() {
		observation = Observation.createNotStarted("observation", observationRegistry).start();

		rootBucketRetriever = new RootBucketRetriever(VIEW_NAME, bucketRepository, observationRegistry);
		rootBucket = new Bucket(BucketDescriptor.empty(), VIEW_NAME);
		when(bucketRepository.retrieveRootBucket(VIEW_NAME)).thenReturn(Optional.of(rootBucket));
	}

	@Test
	void should_FetchRootFragment_when_NotYetInMemory() {
		final Bucket result = rootBucketRetriever.retrieveRootBucket(observation);

		assertEquals(rootBucket, result);
		verify(bucketRepository).retrieveRootBucket(VIEW_NAME);
	}

	@Test
	void should_ReturnFragmentFromMemory_when_FetchedEarlier() {
		rootBucketRetriever.retrieveRootBucket(observation);
		rootBucketRetriever.retrieveRootBucket(observation);
		rootBucketRetriever.retrieveRootBucket(observation);
		final Bucket result = rootBucketRetriever.retrieveRootBucket(observation);

		assertEquals(rootBucket, result);
		verify(bucketRepository).retrieveRootBucket(VIEW_NAME);
	}

	@Test
	void should_ThrowException_when_RootFragmentIsNotFound() {
		when(bucketRepository.retrieveRootBucket(VIEW_NAME)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> rootBucketRetriever.retrieveRootBucket(observation))
				.isInstanceOf(MissingRootFragmentException.class)
						.hasMessage("Could not retrieve root fragment for view %s", VIEW_NAME.asString());
	}
}
