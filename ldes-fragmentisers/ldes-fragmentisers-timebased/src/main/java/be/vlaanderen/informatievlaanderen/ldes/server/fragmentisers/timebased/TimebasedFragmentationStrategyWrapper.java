package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebased;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentationStrategy;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.NonCriticalTasksExecutor;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.relations.TreeRelationsRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.services.FragmentationStrategyWrapper;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ConfigProperties;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebased.config.TimebasedFragmentationConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebased.services.OpenFragmentProvider;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebased.services.TimeBasedFragmentCreator;
import io.micrometer.observation.ObservationRegistry;
import org.apache.jena.rdf.model.Property;
import org.springframework.context.ApplicationContext;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.PROV_GENERATED_AT_TIME;
import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebased.config.TimebasedProperties.FRAGMENTATION_PROPERTY;
import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebased.config.TimebasedProperties.MEMBER_LIMIT;
import static org.apache.jena.rdf.model.ResourceFactory.createProperty;

public class TimebasedFragmentationStrategyWrapper implements FragmentationStrategyWrapper {

	public FragmentationStrategy wrapFragmentationStrategy(ApplicationContext applicationContext,
			FragmentationStrategy fragmentationStrategy, ConfigProperties fragmentationProperties) {
		LdesFragmentRepository ldesFragmentRepository = applicationContext.getBean(LdesFragmentRepository.class);
		ObservationRegistry observationRegistry = applicationContext.getBean(ObservationRegistry.class);
		TreeRelationsRepository treeRelationsRepository = applicationContext
				.getBean(TreeRelationsRepository.class);
		NonCriticalTasksExecutor nonCriticalTasksExecutor = applicationContext.getBean(NonCriticalTasksExecutor.class);

		OpenFragmentProvider openFragmentProvider = getOpenFragmentProvider(fragmentationProperties,
				ldesFragmentRepository, treeRelationsRepository, nonCriticalTasksExecutor);
		return new TimebasedFragmentationStrategy(fragmentationStrategy,
				openFragmentProvider, observationRegistry, treeRelationsRepository);

	}

	private OpenFragmentProvider getOpenFragmentProvider(ConfigProperties properties,
			LdesFragmentRepository ldesFragmentRepository, TreeRelationsRepository treeRelationsRepository,
			NonCriticalTasksExecutor nonCriticalTasksExecutor) {
		TimebasedFragmentationConfig timebasedFragmentationConfig = createTimebasedFragmentationConfig(properties);
		TimeBasedFragmentCreator timeBasedFragmentCreator = getTimeBasedFragmentCreator(
				ldesFragmentRepository, treeRelationsRepository, nonCriticalTasksExecutor,
				timebasedFragmentationConfig.fragmentationProperty());
		return new OpenFragmentProvider(timeBasedFragmentCreator, ldesFragmentRepository,
				timebasedFragmentationConfig.memberLimit());
	}

	private TimeBasedFragmentCreator getTimeBasedFragmentCreator(LdesFragmentRepository ldesFragmentRepository,
			TreeRelationsRepository treeRelationsRepository,
			NonCriticalTasksExecutor nonCriticalTasksExecutor, Property timebasedFragmentationConfig) {
		return new TimeBasedFragmentCreator(
				ldesFragmentRepository, treeRelationsRepository, nonCriticalTasksExecutor,
				timebasedFragmentationConfig);
	}

	private TimebasedFragmentationConfig createTimebasedFragmentationConfig(ConfigProperties properties) {
		return new TimebasedFragmentationConfig(Long.valueOf(properties.get(MEMBER_LIMIT)),
				createProperty(properties.getOrDefault(FRAGMENTATION_PROPERTY, PROV_GENERATED_AT_TIME)));
	}
}
