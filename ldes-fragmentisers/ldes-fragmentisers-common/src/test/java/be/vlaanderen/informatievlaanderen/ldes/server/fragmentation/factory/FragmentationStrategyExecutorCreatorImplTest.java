package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.factory;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.FragmentationStrategy;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.FragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.MembersToFragmentRepository;
import io.micrometer.observation.ObservationRegistry;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class FragmentationStrategyExecutorCreatorImplTest {

	@InjectMocks
	private FragmentationStrategyExecutorCreatorImpl fragmentationStrategyExecutorCreator;

	@Test
	void when_CreateExecutorIsCalled_should_ReturnCompleteExecutor() {
		ViewName viewName = new ViewName("collection", "view");
		FragmentationStrategy fragmentationStrategy = mock(FragmentationStrategy.class);

		var strategyExecutor = fragmentationStrategyExecutorCreator.createExecutor(viewName, fragmentationStrategy);

		assertEquals(viewName, strategyExecutor.getViewName());
	}

}