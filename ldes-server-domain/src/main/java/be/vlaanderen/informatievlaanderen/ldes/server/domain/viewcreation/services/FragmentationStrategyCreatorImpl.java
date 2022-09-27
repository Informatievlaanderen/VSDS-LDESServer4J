package be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentationStrategy;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentationStrategyImpl;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.RootFragmentCreator;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.FragmentationConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewSpecification;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FragmentationStrategyCreatorImpl implements FragmentationStrategyCreator {
	private final ApplicationContext applicationContext;
	private final LdesFragmentRepository ldesFragmentRepository;
	private final RootFragmentCreator rootFragmentCreator;
	private final Tracer tracer;

	public FragmentationStrategyCreatorImpl(ApplicationContext applicationContext,
			LdesFragmentRepository ldesFragmentRepository, RootFragmentCreator rootFragmentCreator, Tracer tracer) {
		this.applicationContext = applicationContext;
		this.ldesFragmentRepository = ldesFragmentRepository;
		this.rootFragmentCreator = rootFragmentCreator;
		this.tracer = tracer;
	}

	public FragmentationStrategy createFragmentationStrategyForView(ViewSpecification viewSpecification) {
		rootFragmentCreator.createRootFragmentForView(viewSpecification.getName());
		FragmentationStrategy fragmentationStrategy = new FragmentationStrategyImpl(ldesFragmentRepository, tracer);
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
