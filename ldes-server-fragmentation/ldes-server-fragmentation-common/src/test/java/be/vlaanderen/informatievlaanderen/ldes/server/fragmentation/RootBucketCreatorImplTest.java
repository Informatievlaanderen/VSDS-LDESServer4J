package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Bucket;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.factory.RootBucketCreatorImpl;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.BucketRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RootBucketCreatorImplTest {
	private static final ViewName VIEW_NAME = new ViewName("collectionName", "mobility-hindrances");
	@Mock
	private BucketRepository bucketRepository;
	@InjectMocks
	private RootBucketCreatorImpl rootBucketCreator;

	@Test
	void when_RootFragmentDoesNotExist_ItIsCreatedAndSaved() {
		rootBucketCreator.createRootBucketForView(VIEW_NAME);

		InOrder inOrder = inOrder(bucketRepository);
		inOrder.verify(bucketRepository).retrieveRootBucket(VIEW_NAME);
		inOrder.verify(bucketRepository).insertRootBucket(Bucket.createRootBucketForView(VIEW_NAME));
		inOrder.verifyNoMoreInteractions();
	}

	@Test
	void when_RootFragmentExists_NothingHappens() {
		when(bucketRepository.retrieveRootBucket(VIEW_NAME)).thenReturn(Optional.of(mock(Bucket.class)));

		rootBucketCreator.createRootBucketForView(VIEW_NAME);

		InOrder inOrder = inOrder(bucketRepository);
		inOrder.verify(bucketRepository).retrieveRootBucket(VIEW_NAME);
		inOrder.verifyNoMoreInteractions();
	}
}