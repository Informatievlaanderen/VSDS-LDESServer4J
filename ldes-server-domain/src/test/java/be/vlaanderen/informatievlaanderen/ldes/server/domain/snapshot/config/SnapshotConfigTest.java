package be.vlaanderen.informatievlaanderen.ldes.server.domain.snapshot.config;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentationStrategy;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.NonCriticalTasksExecutor;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.repository.MemberRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.services.FragmentationStrategyWrapper;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ConfigProperties;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.view.service.ViewServiceImpl.DEFAULT_VIEW_FRAGMENTATION_PROPERTIES;
import static be.vlaanderen.informatievlaanderen.ldes.server.domain.view.service.ViewServiceImpl.DEFAULT_VIEW_FRAGMENTATION_STRATEGY;
import static org.mockito.Mockito.*;

class SnapshotConfigTest {
	private final ApplicationContext applicationContext = mock(ApplicationContext.class);
	private final LdesFragmentRepository ldesFragmentRepository = mock(LdesFragmentRepository.class);
	private final MemberRepository memberRepository = mock(MemberRepository.class);
	private final NonCriticalTasksExecutor nonCriticalTasksExecutor = mock(NonCriticalTasksExecutor.class);
	private final ApplicationEventPublisher applicationEventPublisher = mock(ApplicationEventPublisher.class);

	@Test
	void test() {
		FragmentationStrategyWrapper fragmentationStrategyWrapper = mock(FragmentationStrategyWrapper.class);
		when(applicationContext.getBean(DEFAULT_VIEW_FRAGMENTATION_STRATEGY)).thenReturn(fragmentationStrategyWrapper);

		SnapshotConfig snapshotConfig = new SnapshotConfig();
		FragmentationStrategy fragmentationStrategy = snapshotConfig.snapshotFragmentationStrategy(applicationContext,
				ldesFragmentRepository, memberRepository, nonCriticalTasksExecutor, applicationEventPublisher);

		verify(fragmentationStrategyWrapper).wrapFragmentationStrategy(eq(applicationContext),
				any(FragmentationStrategy.class), eq(new ConfigProperties(DEFAULT_VIEW_FRAGMENTATION_PROPERTIES)));

	}

}
