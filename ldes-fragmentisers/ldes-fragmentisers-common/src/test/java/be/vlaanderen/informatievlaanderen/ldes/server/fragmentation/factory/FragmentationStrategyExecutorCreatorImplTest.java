package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.factory;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewSpecification;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.FragmentationStrategy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FragmentationStrategyExecutorCreatorImplTest {

	@Mock
	private FragmentationStrategyCreator fragmentationStrategyCreator;

	@InjectMocks
	private FragmentationStrategyExecutorCreatorImpl fragmentationStrategyExecutorCreator;

	@Test
	void when_CreateExecutorIsCalled_should_ReturnCompleteExecutor() {
		ViewName viewName = new ViewName("collection", "view");
		FragmentationStrategy fragmentationStrategy = mock(FragmentationStrategy.class);
		ViewSpecification viewSpecification = mock(ViewSpecification.class);
		when(fragmentationStrategyCreator.createFragmentationStrategyForView(viewSpecification))
				.thenReturn(fragmentationStrategy);

		var strategyExecutor = fragmentationStrategyExecutorCreator.createExecutor(viewName, viewSpecification);

		assertEquals(viewName, strategyExecutor.getViewName());
	}

}
