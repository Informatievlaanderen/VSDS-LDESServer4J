package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.entities.ViewSpecification;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.FragmentationConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.AllocationRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.FragmentRepository;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FragmentationStrategyCreatorImpl implements FragmentationStrategyCreator {
	private final ApplicationContext applicationContext;
	private final FragmentRepository fragmentRepository;
	private final RootFragmentCreator rootFragmentCreator;
	private final AllocationRepository allocationRepository;

	public FragmentationStrategyCreatorImpl(ApplicationContext applicationContext,
			FragmentRepository fragmentRepository,
			RootFragmentCreator rootFragmentCreator,
			AllocationRepository allocationRepository) {
		this.applicationContext = applicationContext;
		this.fragmentRepository = fragmentRepository;
		this.rootFragmentCreator = rootFragmentCreator;
		this.allocationRepository = allocationRepository;
	}

	public FragmentationStrategy createFragmentationStrategyForView(ViewSpecification viewSpecification) {
		rootFragmentCreator.createRootFragmentForView(viewSpecification.getName());
		NonCriticalTasksExecutor nonCriticalTasksExecutor = applicationContext.getBean(NonCriticalTasksExecutor.class);
		FragmentationStrategy fragmentationStrategy = new FragmentationStrategyImpl(fragmentRepository,
				allocationRepository, nonCriticalTasksExecutor);
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
