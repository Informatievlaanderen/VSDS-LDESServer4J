package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebased;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ConfigProperties;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.FragmentationStrategy;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.FragmentationStrategyWrapper;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.FragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebased.config.TimebasedFragmentationConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebased.services.OpenFragmentProvider;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebased.services.TimeBasedFragmentCreator;
import io.micrometer.observation.ObservationRegistry;
import org.apache.jena.rdf.model.Property;
import org.springframework.context.ApplicationContext;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.PROV_GENERATED_AT_TIME;
import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebased.config.TimebasedProperties.FRAGMENTATION_PATH;
import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebased.config.TimebasedProperties.MEMBER_LIMIT;
import static org.apache.jena.rdf.model.ResourceFactory.createProperty;

public class TimebasedFragmentationStrategyWrapper implements FragmentationStrategyWrapper {

	public FragmentationStrategy wrapFragmentationStrategy(ApplicationContext applicationContext,
			FragmentationStrategy fragmentationStrategy, ConfigProperties fragmentationProperties) {
		FragmentRepository fragmentRepository = applicationContext.getBean(FragmentRepository.class);
		ObservationRegistry observationRegistry = applicationContext.getBean(ObservationRegistry.class);

		OpenFragmentProvider openFragmentProvider = getOpenFragmentProvider(fragmentationProperties,
				fragmentRepository);
		return new TimebasedFragmentationStrategy(fragmentationStrategy,
				openFragmentProvider, observationRegistry, fragmentRepository);

	}

	private OpenFragmentProvider getOpenFragmentProvider(ConfigProperties properties,
			FragmentRepository fragmentRepository) {
		TimebasedFragmentationConfig timebasedFragmentationConfig = createTimebasedFragmentationConfig(properties);
		TimeBasedFragmentCreator timeBasedFragmentCreator = getTimeBasedFragmentCreator(
				fragmentRepository,
				timebasedFragmentationConfig.fragmentationPath());
		return new OpenFragmentProvider(timeBasedFragmentCreator, fragmentRepository,
				timebasedFragmentationConfig.memberLimit());
	}

	private TimeBasedFragmentCreator getTimeBasedFragmentCreator(FragmentRepository fragmentRepository,
			Property timebasedFragmentationConfig) {
		return new TimeBasedFragmentCreator(
				fragmentRepository,
				timebasedFragmentationConfig);
	}

	private TimebasedFragmentationConfig createTimebasedFragmentationConfig(ConfigProperties properties) {
		return new TimebasedFragmentationConfig(Long.valueOf(properties.get(MEMBER_LIMIT)),
				createProperty(properties.getOrDefault(FRAGMENTATION_PATH, PROV_GENERATED_AT_TIME)));
	}
}
