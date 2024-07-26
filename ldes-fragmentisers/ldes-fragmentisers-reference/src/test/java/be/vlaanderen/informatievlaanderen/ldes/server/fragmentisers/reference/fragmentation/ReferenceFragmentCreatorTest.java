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
class ReferenceFragmentCreatorTest {

	private static final ViewName viewName = new ViewName("collectionName", "view");
	private static final BucketDescriptorPair timebasedPair = new BucketDescriptorPair("year", "2023");
	private static final BucketDescriptorPair referenceRootPair = new BucketDescriptorPair(DEFAULT_FRAGMENTATION_KEY, FRAGMENT_KEY_REFERENCE_ROOT);
	private static final BucketDescriptorPair referencePair = new BucketDescriptorPair(DEFAULT_FRAGMENTATION_KEY, RDF.type.getURI());
	private static final BucketDescriptorPair defaultPair = new BucketDescriptorPair(DEFAULT_FRAGMENTATION_KEY, DEFAULT_BUCKET_STRING);
	@Mock
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
		BucketDescriptor bucketDescriptor = bucket.createChild(referencePair).getBucketDescriptor();

		when(bucketRepository.retrieveBucket(viewName, bucketDescriptor)).thenReturn(Optional.empty());

		Bucket childBucket = referenceBucketCreator.getOrCreateBucket(bucket, RDF.type.getURI(), rootBucket);

		assertThat(childBucket.getBucketDescriptorAsString())
				.isEqualTo(BucketDescriptor.of(timebasedPair, referencePair).asDecodedString());
		verify(bucketRepository).retrieveBucket(viewName, bucketDescriptor);
		verify(bucketRepository).insertBucket(childBucket);
	}

	@Test
	void when_ReferenceFragmentDoesExist_RetrievedReferenceFragmentIsReturned() {
		Bucket bucket = new Bucket(BucketDescriptor.of(timebasedPair), viewName);
		Bucket rootBucket = bucket.createChild(referenceRootPair);
		Bucket tileBucket = bucket.createChild(referencePair);

		when(bucketRepository.retrieveBucket(viewName, tileBucket.getBucketDescriptor()))
				.thenReturn(Optional.of(tileBucket));
		Bucket childBucket = referenceBucketCreator.getOrCreateBucket(bucket, RDF.type.getURI(), rootBucket);

		assertThat(childBucket.getBucketDescriptorAsString())
				.isEqualTo(BucketDescriptor.of(timebasedPair, referencePair).asDecodedString());
		verify(bucketRepository).retrieveBucket(viewName, tileBucket.getBucketDescriptor());
		verifyNoMoreInteractions(bucketRepository);
	}

	@Test
	void when_RootFragmentDoesNotExist_NewRootFragmentIsCreatedAndSaved() {
		Bucket bucket = new Bucket(BucketDescriptor.of(timebasedPair), viewName);
		Bucket rootBucket = bucket.createChild(referenceRootPair);

		when(bucketRepository.retrieveBucket(viewName, rootBucket.getBucketDescriptor())).thenReturn(Optional.empty());

		Bucket returnedBucket = referenceBucketCreator.getOrCreateRootBucket(bucket, FRAGMENT_KEY_REFERENCE_ROOT);

		assertThat(returnedBucket.getBucketDescriptorAsString())
				.isEqualTo(BucketDescriptor.of(timebasedPair, referenceRootPair).asDecodedString());
		verify(bucketRepository).retrieveBucket(viewName, rootBucket.getBucketDescriptor());
		verify(bucketRepository).insertBucket(returnedBucket);
	}

	@Test
	void when_RootFragmentDoesNotExist_RetrievedRootFragmentIsReturned() {
		Bucket bucket = new Bucket(BucketDescriptor.of(timebasedPair), viewName);
		Bucket rootBucket = bucket.createChild(referenceRootPair);
		when(bucketRepository.retrieveBucket(viewName, rootBucket.getBucketDescriptor())).thenReturn(Optional.of(rootBucket));

		Bucket returnedBucket = referenceBucketCreator.getOrCreateBucket(bucket, FRAGMENT_KEY_REFERENCE_ROOT, rootBucket);

		assertThat(returnedBucket.getBucketDescriptorAsString())
				.isEqualTo(BucketDescriptor.of(timebasedPair, referenceRootPair).asDecodedString());
		verify(bucketRepository, times(1)).retrieveBucket(viewName, rootBucket.getBucketDescriptor());
		verifyNoMoreInteractions(bucketRepository);
	}

	@Test
	void when_DefaultFragmentDoesNotExist_DefaultFragmentIsCreatedAndSaved() {
		Bucket bucket = new Bucket(BucketDescriptor.of(timebasedPair), viewName);
		Bucket rootBucket = bucket.createChild(referenceRootPair);
		BucketDescriptor bucketDescriptor = bucket.createChild(defaultPair).getBucketDescriptor();
		when(bucketRepository.retrieveBucket(viewName, bucketDescriptor)).thenReturn(Optional.empty());

		Bucket childBucket = referenceBucketCreator.getOrCreateBucket(bucket, DEFAULT_BUCKET_STRING, rootBucket);

		assertThat(childBucket.getBucketDescriptorAsString())
				.isEqualTo(BucketDescriptor.of(timebasedPair, defaultPair).asDecodedString());
		verify(bucketRepository).retrieveBucket(viewName, bucketDescriptor);
		verify(bucketRepository).insertBucket(childBucket);
	}

	@Test
	void when_DefaultFragmentDoesExist_RetrievedDefaultFragmentIsReturned() {
		Bucket bucket = new Bucket(BucketDescriptor.of(timebasedPair), viewName);
		Bucket rootBucket = bucket.createChild(referenceRootPair);
		Bucket tileBucket = bucket.createChild(defaultPair);

		when(bucketRepository.retrieveBucket(viewName, tileBucket.getBucketDescriptor())).thenReturn(Optional.of(tileBucket));

		Bucket childBucket = referenceBucketCreator.getOrCreateBucket(bucket, DEFAULT_BUCKET_STRING, rootBucket);

		assertThat(childBucket.getBucketDescriptorAsString())
				.isEqualTo(BucketDescriptor.of(timebasedPair, defaultPair).asDecodedString());
		verify(bucketRepository).retrieveBucket(viewName, tileBucket.getBucketDescriptor());
		verifyNoMoreInteractions(bucketRepository);
	}

}