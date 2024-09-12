package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Bucket;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.FragmentationMember;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.valueobjects.BucketDescriptor;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.valueobjects.BucketDescriptorPair;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.valueobjects.BucketRelation;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.valueobjects.BucketRelationCreatedEvent;
import io.micrometer.observation.Observation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class FragmentationStrategyDecoratorTest {
	private static final ViewName VIEW_NAME = new ViewName("collectionName", "view");
	@Mock
	private FragmentationStrategy fragmentationStrategy;
	@Mock
	private ApplicationEventPublisher applicationEventPublisher;
	@InjectMocks
	private FragmentationStrategyDecoratorTestImpl fragmentationStrategyDecorator;

	@Test
	void when_ParentDoesNotYetHaveRelationToChild_AddRelationAndSaveToDatabase() {
		Bucket parentBucket = new Bucket(BucketDescriptor.empty(), VIEW_NAME);
		Bucket childBucket = parentBucket.createChild(new BucketDescriptorPair("key", "value"));

		fragmentationStrategyDecorator.addRelationFromParentToChild(parentBucket, childBucket);

		verify(applicationEventPublisher).publishEvent(new BucketRelationCreatedEvent(BucketRelation.createGenericRelation(parentBucket, childBucket)));
	}

	@Test
	void when_DecoratorAddsMemberToBucket_WrappedFragmentationStrategyIsCalled() {
		Bucket parentBucket = new Bucket(BucketDescriptor.empty(), VIEW_NAME);
		FragmentationMember member = mock(FragmentationMember.class);
		Observation span = mock(Observation.class);

		fragmentationStrategyDecorator.addMemberToBucketAndReturnMembers(parentBucket, member, span);

		verify(fragmentationStrategy).addMemberToBucketAndReturnMembers(parentBucket, member, span);
	}

	static class FragmentationStrategyDecoratorTestImpl extends FragmentationStrategyDecorator {
		protected FragmentationStrategyDecoratorTestImpl(FragmentationStrategy fragmentationStrategy,
		                                                 ApplicationEventPublisher applicationEventPublisher) {
			super(fragmentationStrategy, applicationEventPublisher);
		}
	}
}
