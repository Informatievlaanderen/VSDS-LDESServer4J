package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Bucket;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.FragmentationMember;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.valueobjects.BucketDescriptor;
import io.micrometer.observation.Observation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class FragmentationStrategyDecoratorTest {
	private static final ViewName VIEW_NAME = new ViewName("collectionName", "view");
	@Mock
	private FragmentationStrategy fragmentationStrategy;
	@InjectMocks
	private FragmentationStrategyDecoratorTestImpl fragmentationStrategyDecorator;


	@Test
	void when_DecoratorAddsMemberToBucket_WrappedFragmentationStrategyIsCalled() {
		Bucket parentBucket = new Bucket(BucketDescriptor.empty(), VIEW_NAME);
		FragmentationMember member = mock(FragmentationMember.class);
		Observation span = mock(Observation.class);

		fragmentationStrategyDecorator.addMemberToBucketAndReturnMembers(parentBucket, member, span);

		verify(fragmentationStrategy).addMemberToBucketAndReturnMembers(parentBucket, member, span);
	}

	static class FragmentationStrategyDecoratorTestImpl extends FragmentationStrategyDecorator {
		protected FragmentationStrategyDecoratorTestImpl(FragmentationStrategy fragmentationStrategy) {
			super(fragmentationStrategy);
		}
	}
}
