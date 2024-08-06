package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.reference.fragmentation;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Bucket;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.BucketRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.valueobjects.BucketDescriptor;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.valueobjects.BucketDescriptorPair;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.reference.relations.ReferenceFragmentRelationsAttributer;
import org.apache.jena.vocabulary.RDF;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.ServerConstants.DEFAULT_BUCKET_STRING;
import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.reference.ReferenceFragmentationStrategyWrapper.DEFAULT_FRAGMENTATION_KEY;
import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.reference.fragmentation.ReferenceBucketCreator.FRAGMENT_KEY_REFERENCE_ROOT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReferenceBucketCreatorTest {
	private static final ViewName viewName = new ViewName("collectionName", "view");
	private static final BucketDescriptorPair timebasedPair = new BucketDescriptorPair("year", "2023");
	private static final BucketDescriptorPair referenceRootPair = new BucketDescriptorPair(DEFAULT_FRAGMENTATION_KEY, FRAGMENT_KEY_REFERENCE_ROOT);
	private static final BucketDescriptorPair referencePair = new BucketDescriptorPair(DEFAULT_FRAGMENTATION_KEY, RDF.type.getURI());
	private static final BucketDescriptorPair defaultPair = new BucketDescriptorPair(DEFAULT_FRAGMENTATION_KEY, DEFAULT_BUCKET_STRING);

	private ReferenceBucketCreator referenceBucketCreator;

	@Mock
	private BucketRepository bucketRepository;

	@Mock
	private ReferenceFragmentRelationsAttributer relationsAttributer;

	@BeforeEach
	void setUp() {
		referenceBucketCreator =
				new ReferenceBucketCreator(bucketRepository, relationsAttributer, DEFAULT_FRAGMENTATION_KEY);
	}

	@Test
	void when_ReferenceFragmentDoesNotExist_NewReferenceFragmentIsCreatedAndSaved() {
		Bucket bucket = new Bucket(BucketDescriptor.of(timebasedPair), viewName);
		Bucket rootBucket = bucket.createChild(referenceRootPair);
		Bucket childBucket = bucket.createChild(referencePair);

		when(bucketRepository.retrieveBucket(viewName, childBucket.getBucketDescriptor())).thenReturn(Optional.empty());
		when(bucketRepository.insertBucket(any())).thenReturn(childBucket);

		Bucket returnedBucket = referenceBucketCreator.getOrCreateBucket(bucket, RDF.type.getURI(), rootBucket);

		assertThat(returnedBucket)
				.describedAs("Child instance must be the same, to assure the bucket instance from the db is returned")
				.isSameAs(childBucket);
		assertThat(returnedBucket.getBucketDescriptorAsString()).isEqualTo("year=2023&reference=%s", RDF.type.getURI());
		verify(bucketRepository).retrieveBucket(viewName, childBucket.getBucketDescriptor());
		verify(bucketRepository).insertBucket(returnedBucket);
	}

	@Test
	void when_ReferenceFragmentDoesExist_RetrievedReferenceFragmentIsReturned() {
		Bucket bucket = new Bucket(BucketDescriptor.of(timebasedPair), viewName);
		Bucket rootBucket = bucket.createChild(referenceRootPair);
		Bucket referenceBucket = bucket.createChild(referencePair);

		when(bucketRepository.retrieveBucket(viewName, referenceBucket.getBucketDescriptor())).thenReturn(Optional.of(referenceBucket));

		Bucket childBucket = referenceBucketCreator.getOrCreateBucket(bucket, RDF.type.getURI(), rootBucket);

		assertThat(childBucket.getBucketDescriptorAsString()).isEqualTo("year=2023&reference=%s", RDF.type.getURI());
		verify(bucketRepository).retrieveBucket(viewName, referenceBucket.getBucketDescriptor());
		verifyNoMoreInteractions(bucketRepository);
	}

	@Test
	void when_RootFragmentDoesNotExist_NewRootFragmentIsCreatedAndSaved() {
		Bucket bucket = new Bucket(BucketDescriptor.of(timebasedPair), viewName);
		Bucket rootBucket = bucket.createChild(referenceRootPair);

		when(bucketRepository.retrieveBucket(viewName, rootBucket.getBucketDescriptor())).thenReturn(Optional.empty());
		when(bucketRepository.insertBucket(any())).thenReturn(rootBucket);

		Bucket returnedBucket = referenceBucketCreator.getOrCreateRootBucket(bucket, FRAGMENT_KEY_REFERENCE_ROOT);

		assertThat(returnedBucket)
				.describedAs("Child instance must be the same, to assure the bucket instance from the db is returned")
				.isSameAs(rootBucket);
		verify(bucketRepository).retrieveBucket(viewName, rootBucket.getBucketDescriptor());
		verify(bucketRepository).insertBucket(returnedBucket);
	}

	@Test
	void when_RootFragmentDoesNotExist_RetrievedRootFragmentIsReturned() {
		Bucket bucket = new Bucket(BucketDescriptor.of(timebasedPair), viewName);
		Bucket rootBucket = bucket.createChild(referenceRootPair);
		when(bucketRepository.retrieveBucket(viewName, rootBucket.getBucketDescriptor())).thenReturn(Optional.of(rootBucket));

		Bucket returnedBucket = referenceBucketCreator.getOrCreateBucket(bucket, FRAGMENT_KEY_REFERENCE_ROOT, rootBucket);

		assertThat(returnedBucket.getBucketDescriptorAsString()).isEqualTo("year=2023&reference=");
		verify(bucketRepository).retrieveBucket(viewName, rootBucket.getBucketDescriptor());
		verifyNoMoreInteractions(bucketRepository);
	}

	@Test
	void when_DefaultFragmentDoesNotExist_DefaultFragmentIsCreatedAndSaved() {
		Bucket bucket = new Bucket(BucketDescriptor.of(timebasedPair), viewName);
		Bucket rootBucket = bucket.createChild(referenceRootPair);
		Bucket defaultBucket = bucket.createChild(defaultPair);

		when(bucketRepository.retrieveBucket(viewName, defaultBucket.getBucketDescriptor())).thenReturn(Optional.empty());
		when(bucketRepository.insertBucket(any())).thenReturn(defaultBucket);

		Bucket childBucket = referenceBucketCreator.getOrCreateBucket(bucket, DEFAULT_BUCKET_STRING, rootBucket);

		assertThat(childBucket)
				.describedAs("Child instance must be the same, to assure the bucket instance from the db is returned")
				.isSameAs(defaultBucket);
		verify(bucketRepository).retrieveBucket(viewName, defaultBucket.getBucketDescriptor());
		verify(bucketRepository).insertBucket(childBucket);
	}

	@Test
	void when_DefaultFragmentDoesExist_RetrievedDefaultFragmentIsReturned() {
		Bucket bucket = new Bucket(BucketDescriptor.of(timebasedPair), viewName);
		Bucket rootBucket = bucket.createChild(referenceRootPair);
		Bucket defaultBucket = bucket.createChild(defaultPair);

		when(bucketRepository.retrieveBucket(viewName, defaultBucket.getBucketDescriptor())).thenReturn(Optional.of(defaultBucket));

		Bucket childBucket = referenceBucketCreator.getOrCreateBucket(bucket, DEFAULT_BUCKET_STRING, rootBucket);

		assertThat(childBucket.getBucketDescriptorAsString()).isEqualTo("year=2023&reference=unknown");
		verify(bucketRepository).retrieveBucket(viewName, defaultBucket.getBucketDescriptor());
		verifyNoMoreInteractions(bucketRepository);
	}

}