package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.factory;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.entities.ViewSpecification;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.FragmentationConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.FragmentationStrategy;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.FragmentationStrategyImpl;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.FragmentationStrategyWrapper;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.NonCriticalTasksExecutor;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.FragmentRepository;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class FragmentationStrategyCreatorImpl implements FragmentationStrategyCreator {
	public static final String PAGINATION_FRAGMENTATION = "PaginationFragmentation";
	private final ApplicationContext applicationContext;
	private final FragmentRepository fragmentRepository;
	private final RootFragmentCreator rootFragmentCreator;
	private final ApplicationEventPublisher eventPublisher;

	public FragmentationStrategyCreatorImpl(ApplicationContext applicationContext,
			FragmentRepository fragmentRepository,
			RootFragmentCreator rootFragmentCreator,
			ApplicationEventPublisher eventPublisher) {
		this.applicationContext = applicationContext;
		this.fragmentRepository = fragmentRepository;
		this.rootFragmentCreator = rootFragmentCreator;
		this.eventPublisher = eventPublisher;
	}

	public FragmentationStrategy createFragmentationStrategyForView(ViewSpecification viewSpecification) {
		rootFragmentCreator.createRootFragmentForView(viewSpecification.getName());
		NonCriticalTasksExecutor nonCriticalTasksExecutor = applicationContext.getBean(NonCriticalTasksExecutor.class);
		FragmentationStrategy fragmentationStrategy = new FragmentationStrategyImpl(fragmentRepository,
				nonCriticalTasksExecutor, eventPublisher);
		FragmentationStrategyWrapper paginationWrapper = (FragmentationStrategyWrapper) applicationContext
				.getBean(PAGINATION_FRAGMENTATION);
		fragmentationStrategy = paginationWrapper.wrapFragmentationStrategy(applicationContext, fragmentationStrategy,
				viewSpecification.getPaginationProperties());
		if (viewSpecification.getFragmentations() != null) {
			fragmentationStrategy = wrapFragmentationStrategy(viewSpecification.getFragmentations(),
					fragmentationStrategy);
		}
		return fragmentationStrategy;
	}

	private FragmentationStrategy wrapFragmentationStrategy(List<FragmentationConfig> fragmentationConfigs,
			FragmentationStrategy fragmentationStrategy) {
		for (int i = fragmentationConfigs.size() - 1; i >= 0; i--) {
			FragmentationConfig currentFragmentationConfig = fragmentationConfigs.get(i);
			fragmentationStrategy = wrapFragmentationStrategyUsingFragmentationConfig(fragmentationStrategy,
					currentFragmentationConfig);
		}
		return fragmentationStrategy;
	}

	private FragmentationStrategy wrapFragmentationStrategyUsingFragmentationConfig(
			FragmentationStrategy fragmentationStrategy, FragmentationConfig currentFragmentationConfig) {
		FragmentationStrategyWrapper fragmentationStrategyWrapper = (FragmentationStrategyWrapper) applicationContext
				.getBean(currentFragmentationConfig.getName());
		fragmentationStrategy = fragmentationStrategyWrapper.wrapFragmentationStrategy(applicationContext,
				fragmentationStrategy, currentFragmentationConfig.getProperties());
		return fragmentationStrategy;
	}
}
