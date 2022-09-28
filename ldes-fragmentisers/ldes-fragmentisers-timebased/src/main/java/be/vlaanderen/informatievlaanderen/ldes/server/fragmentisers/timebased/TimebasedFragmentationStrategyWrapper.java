package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebased;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.FragmentationProperties;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.config.LdesConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentationStrategy;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.services.FragmentationStrategyWrapper;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.context.ApplicationContext;

import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebased.TimebasedProperties.MEMBER_LIMIT;

public class TimebasedFragmentationStrategyWrapper implements FragmentationStrategyWrapper {

	public FragmentationStrategy wrapFragmentationStrategy(ApplicationContext applicationContext,
			FragmentationStrategy fragmentationStrategy, FragmentationProperties properties) {
		LdesConfig ldesConfig = applicationContext.getBean(LdesConfig.class);
		LdesFragmentRepository ldesFragmentRepository1 = applicationContext.getBean(LdesFragmentRepository.class);
		TimebasedFragmentationConfig timebasedFragmentationConfig = createTimebasedFragmentationConfig(properties);
		Tracer tracer = applicationContext.getBean(Tracer.class);

		TimeBasedFragmentCreator timeBasedFragmentCreator = new TimeBasedFragmentCreator(ldesConfig,
				timebasedFragmentationConfig,
				ldesFragmentRepository1);
		return new TimebasedFragmentationStrategy(fragmentationStrategy, timeBasedFragmentCreator,
				ldesFragmentRepository1, tracer);

	}

	private TimebasedFragmentationConfig createTimebasedFragmentationConfig(FragmentationProperties properties) {
		TimebasedFragmentationConfig timebasedFragmentationConfig = new TimebasedFragmentationConfig();
		timebasedFragmentationConfig.setMemberLimit(Long.valueOf(properties.get(MEMBER_LIMIT)));
		return timebasedFragmentationConfig;
	}
}
