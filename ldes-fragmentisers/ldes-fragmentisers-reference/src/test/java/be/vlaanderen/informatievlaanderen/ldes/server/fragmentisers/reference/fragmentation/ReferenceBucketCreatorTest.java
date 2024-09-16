package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.reference.fragmentation;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Bucket;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.valueobjects.BucketDescriptor;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.valueobjects.BucketDescriptorPair;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.reference.relations.ReferenceFragmentRelationsAttributer;
import org.apache.jena.vocabulary.RDF;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.ServerConstants.DEFAULT_BUCKET_STRING;
import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.reference.ReferenceFragmentationStrategyWrapper.DEFAULT_FRAGMENTATION_KEY;
import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.reference.fragmentation.ReferenceBucketCreator.FRAGMENT_KEY_REFERENCE_ROOT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class ReferenceBucketCreatorTest {
	private static final ViewName viewName = new ViewName("collectionName", "view");
	private static final BucketDescriptorPair timebasedPair = new BucketDescriptorPair("year", "2023");
	private static final BucketDescriptorPair referenceRootPair = new BucketDescriptorPair(DEFAULT_FRAGMENTATION_KEY, FRAGMENT_KEY_REFERENCE_ROOT);
	private static final BucketDescriptorPair referencePair = new BucketDescriptorPair(DEFAULT_FRAGMENTATION_KEY, RDF.type.getURI());
	private static final BucketDescriptorPair defaultPair = new BucketDescriptorPair(DEFAULT_FRAGMENTATION_KEY, DEFAULT_BUCKET_STRING);
	private ReferenceBucketCreator referenceBucketCreator;
	@Mock
	private ReferenceFragmentRelationsAttributer relationsAttributer;

	@BeforeEach
	void setUp() {
		referenceBucketCreator = new ReferenceBucketCreator(relationsAttributer, DEFAULT_FRAGMENTATION_KEY);
	}

	@Test
	void when_ReferenceFragmentDoesExist_RetrievedReferenceFragmentIsReturned() {
		Bucket bucket = new Bucket(BucketDescriptor.of(timebasedPair), viewName);
		Bucket rootBucket = bucket.createChild(referenceRootPair);
		Bucket referenceBucket = bucket.createChild(referencePair);

		Bucket childBucket = referenceBucketCreator.getOrCreateBucket(bucket, RDF.type.getURI(), rootBucket);

		assertThat(childBucket.getBucketDescriptorAsString()).isEqualTo("year=2023&reference=%s", RDF.type.getURI());
		verify(relationsAttributer).addRelationFromRootToBottom(rootBucket, referenceBucket);
	}

	@Test
	void when_RootFragmentDoesNotExist_NewRootFragmentIsCreatedAndSaved() {
		Bucket bucket = new Bucket(BucketDescriptor.of(timebasedPair), viewName);

		Bucket returnedBucket = referenceBucketCreator.getOrCreateRootBucket(bucket, FRAGMENT_KEY_REFERENCE_ROOT);

		assertThat(returnedBucket.getBucketDescriptorAsString()).isEqualTo("year=2023&reference=");
		verifyNoInteractions(relationsAttributer);
	}

	@Test
	void when_RootFragmentDoesNotExist_RetrievedRootFragmentIsReturned() {
		Bucket bucket = new Bucket(BucketDescriptor.of(timebasedPair), viewName);
		Bucket rootBucket = bucket.createChild(referenceRootPair);

		Bucket returnedBucket = referenceBucketCreator.getOrCreateBucket(bucket, FRAGMENT_KEY_REFERENCE_ROOT, rootBucket);

		assertThat(returnedBucket.getBucketDescriptorAsString()).isEqualTo("year=2023&reference=");
	}

	@Test
	void when_DefaultFragmentDoesExist_RetrievedDefaultFragmentIsReturned() {
		Bucket bucket = new Bucket(BucketDescriptor.of(timebasedPair), viewName);
		Bucket rootBucket = bucket.createChild(referenceRootPair);
		Bucket defaultBucket = bucket.createChild(defaultPair);

		Bucket childBucket = referenceBucketCreator.getOrCreateBucket(bucket, DEFAULT_BUCKET_STRING, rootBucket);

		assertThat(childBucket.getBucketDescriptorAsString()).isEqualTo("year=2023&reference=unknown");
		verify(relationsAttributer).addDefaultRelation(bucket, defaultBucket);
	}

}