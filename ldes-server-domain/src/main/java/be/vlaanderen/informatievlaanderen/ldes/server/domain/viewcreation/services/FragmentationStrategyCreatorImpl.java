package be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentationStrategy;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentationStrategyImpl;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.RootFragmentCreator;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.memberreferences.entities.MemberReferencesRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.FragmentationConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewSpecification;
import io.micrometer.observation.ObservationRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FragmentationStrategyCreatorImpl implements FragmentationStrategyCreator {
	private final ApplicationContext applicationContext;
	private final LdesFragmentRepository ldesFragmentRepository;
	private final MemberReferencesRepository memberReferencesRepository;
	private final RootFragmentCreator rootFragmentCreator;
	private final ObservationRegistry observationRegistry;

	public FragmentationStrategyCreatorImpl(ApplicationContext applicationContext,
			LdesFragmentRepository ldesFragmentRepository,
			MemberReferencesRepository memberReferencesRepository, RootFragmentCreator rootFragmentCreator,
			ObservationRegistry observationRegistry) {
		this.applicationContext = applicationContext;
		this.ldesFragmentRepository = ldesFragmentRepository;
		this.memberReferencesRepository = memberReferencesRepository;
		this.rootFragmentCreator = rootFragmentCreator;
		this.observationRegistry = observationRegistry;
	}

	public FragmentationStrategy createFragmentationStrategyForView(ViewSpecification viewSpecification) {
		rootFragmentCreator.createRootFragmentForView(viewSpecification.getName());
		FragmentationStrategy fragmentationStrategy = new FragmentationStrategyImpl(ldesFragmentRepository,
				memberReferencesRepository, observationRegistry);
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
