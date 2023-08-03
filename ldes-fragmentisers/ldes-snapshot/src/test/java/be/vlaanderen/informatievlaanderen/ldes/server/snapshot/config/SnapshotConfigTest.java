package be.vlaanderen.informatievlaanderen.ldes.server.snapshot.config;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ConfigProperties;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.FragmentationStrategy;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.FragmentationStrategyWrapper;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.NonCriticalTasksExecutor;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.FragmentRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.view.service.ViewServiceImpl.DEFAULT_VIEW_FRAGMENTATION_STRATEGY;
import static be.vlaanderen.informatievlaanderen.ldes.server.snapshot.config.SnapshotConfig.DEFAULT_VIEW_FRAGMENTATION_PROPERTIES;
import static org.mockito.Mockito.*;

class SnapshotConfigTest {
	private final ApplicationContext applicationContext = mock(ApplicationContext.class);
	private final FragmentRepository fragmentRepository = Mockito.mock(FragmentRepository.class);
	private final NonCriticalTasksExecutor nonCriticalTasksExecutor = Mockito.mock(NonCriticalTasksExecutor.class);
	private final ApplicationEventPublisher eventPublisher = mock(ApplicationEventPublisher.class);

	@Test
	void test() {
		FragmentationStrategyWrapper fragmentationStrategyWrapper = mock(FragmentationStrategyWrapper.class);
		when(applicationContext.getBean(DEFAULT_VIEW_FRAGMENTATION_STRATEGY)).thenReturn(fragmentationStrategyWrapper);

		SnapshotConfig snapshotConfig = new SnapshotConfig();
		FragmentationStrategy fragmentationStrategy = snapshotConfig.snapshotFragmentationStrategy(applicationContext,
				fragmentRepository, nonCriticalTasksExecutor,
				eventPublisher);

		verify(fragmentationStrategyWrapper).wrapFragmentationStrategy(eq(applicationContext),
				any(FragmentationStrategy.class), eq(new ConfigProperties(DEFAULT_VIEW_FRAGMENTATION_PROPERTIES)));

	}

}
