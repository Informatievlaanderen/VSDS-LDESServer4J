package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebased;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.NonCriticalTasksExecutor;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentationStrategy;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.treenoderelations.TreeNodeRelationsRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.services.FragmentationStrategyWrapper;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ConfigProperties;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebased.config.TimebasedFragmentationConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebased.services.OpenFragmentProvider;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebased.services.TimeBasedFragmentCreator;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.context.ApplicationContext;

import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebased.config.TimebasedProperties.MEMBER_LIMIT;

public class TimebasedFragmentationStrategyWrapper implements FragmentationStrategyWrapper {

	public FragmentationStrategy wrapFragmentationStrategy(ApplicationContext applicationContext,
			FragmentationStrategy fragmentationStrategy, ConfigProperties fragmentationProperties) {
		LdesFragmentRepository ldesFragmentRepository = applicationContext.getBean(LdesFragmentRepository.class);
		Tracer tracer = applicationContext.getBean(Tracer.class);
		TreeNodeRelationsRepository treeNodeRelationsRepository = applicationContext
				.getBean(TreeNodeRelationsRepository.class);
		NonCriticalTasksExecutor nonCriticalTasksExecutor = applicationContext.getBean(NonCriticalTasksExecutor.class);

		OpenFragmentProvider openFragmentProvider = getOpenFragmentProvider(fragmentationProperties,
				ldesFragmentRepository, treeNodeRelationsRepository, nonCriticalTasksExecutor);
		return new TimebasedFragmentationStrategy(fragmentationStrategy,
				openFragmentProvider, tracer, treeNodeRelationsRepository,
				nonCriticalTasksExecutor);

	}

	private OpenFragmentProvider getOpenFragmentProvider(ConfigProperties properties,
			LdesFragmentRepository ldesFragmentRepository, TreeNodeRelationsRepository treeNodeRelationsRepository,
			NonCriticalTasksExecutor nonCriticalTasksExecutor) {
		TimeBasedFragmentCreator timeBasedFragmentCreator = getTimeBasedFragmentCreator(properties,
				ldesFragmentRepository, treeNodeRelationsRepository, nonCriticalTasksExecutor);
		return new OpenFragmentProvider(timeBasedFragmentCreator, ldesFragmentRepository);
	}

	private TimeBasedFragmentCreator getTimeBasedFragmentCreator(ConfigProperties properties,
			LdesFragmentRepository ldesFragmentRepository, TreeNodeRelationsRepository treeNodeRelationsRepository,
			NonCriticalTasksExecutor nonCriticalTasksExecutor) {
		TimebasedFragmentationConfig timebasedFragmentationConfig = createTimebasedFragmentationConfig(properties);
		return new TimeBasedFragmentCreator(
				timebasedFragmentationConfig,
				ldesFragmentRepository, treeNodeRelationsRepository, nonCriticalTasksExecutor);
	}

	private TimebasedFragmentationConfig createTimebasedFragmentationConfig(ConfigProperties properties) {
		return new TimebasedFragmentationConfig(Long.valueOf(properties.get(MEMBER_LIMIT)));
	}
}
