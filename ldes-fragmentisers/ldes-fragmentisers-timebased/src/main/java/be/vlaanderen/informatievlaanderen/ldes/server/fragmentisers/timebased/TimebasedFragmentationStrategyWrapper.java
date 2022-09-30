package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebased;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.FragmentationProperties;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentationStrategy;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.services.FragmentationStrategyWrapper;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebased.config.TimebasedFragmentationConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebased.services.OpenFragmentProvider;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebased.services.TimeBasedFragmentCreator;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.context.ApplicationContext;

import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebased.config.TimebasedProperties.MEMBER_LIMIT;

public class TimebasedFragmentationStrategyWrapper implements FragmentationStrategyWrapper {

	public FragmentationStrategy wrapFragmentationStrategy(ApplicationContext applicationContext,
			FragmentationStrategy fragmentationStrategy, FragmentationProperties properties) {
		LdesFragmentRepository ldesFragmentRepository = applicationContext.getBean(LdesFragmentRepository.class);
		Tracer tracer = applicationContext.getBean(Tracer.class);

		OpenFragmentProvider openFragmentProvider = getOpenFragmentProvider(properties,
				ldesFragmentRepository);
		return new TimebasedFragmentationStrategy(fragmentationStrategy,
				ldesFragmentRepository, openFragmentProvider, tracer);

	}

	private OpenFragmentProvider getOpenFragmentProvider(FragmentationProperties properties,
			LdesFragmentRepository ldesFragmentRepository) {
		TimeBasedFragmentCreator timeBasedFragmentCreator = getTimeBasedFragmentCreator(properties,
				ldesFragmentRepository);
		return new OpenFragmentProvider(timeBasedFragmentCreator, ldesFragmentRepository);
	}

	private TimeBasedFragmentCreator getTimeBasedFragmentCreator(FragmentationProperties properties,
			LdesFragmentRepository ldesFragmentRepository) {
		TimebasedFragmentationConfig timebasedFragmentationConfig = createTimebasedFragmentationConfig(properties);
		return new TimeBasedFragmentCreator(
				timebasedFragmentationConfig,
				ldesFragmentRepository);
	}

	private TimebasedFragmentationConfig createTimebasedFragmentationConfig(FragmentationProperties properties) {
		TimebasedFragmentationConfig timebasedFragmentationConfig = new TimebasedFragmentationConfig();
		timebasedFragmentationConfig.setMemberLimit(Long.valueOf(properties.get(MEMBER_LIMIT)));
		return timebasedFragmentationConfig;
	}
}
