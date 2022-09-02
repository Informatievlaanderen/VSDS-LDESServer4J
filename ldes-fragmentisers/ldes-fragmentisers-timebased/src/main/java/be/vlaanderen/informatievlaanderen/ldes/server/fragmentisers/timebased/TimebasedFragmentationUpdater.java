package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebased;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.config.FragmentationProperties;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.config.LdesConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentationService;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentationUpdater;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesmember.repository.LdesMemberRepository;
import org.springframework.context.ApplicationContext;

import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebased.TimebasedProperties.MEMBER_LIMIT;

public class TimebasedFragmentationUpdater implements FragmentationUpdater {

	public FragmentationService updateFragmentationService(ApplicationContext applicationContext,
			FragmentationService fragmentationService, FragmentationProperties properties) {
		LdesConfig ldesConfig1 = applicationContext.getBean(LdesConfig.class);
		LdesMemberRepository ldesMemberRepository1 = applicationContext.getBean(LdesMemberRepository.class);
		LdesFragmentRepository ldesFragmentRepository1 = applicationContext.getBean(LdesFragmentRepository.class);
		TimebasedFragmentationConfig timebasedFragmentationConfig = createTimebasedFragmentationConfig(properties);

		TimeBasedFragmentCreator timeBasedFragmentCreator = new TimeBasedFragmentCreator(ldesConfig1,
				timebasedFragmentationConfig,
				ldesMemberRepository1, ldesFragmentRepository1);
		return new TimebasedFragmentationService(fragmentationService, ldesConfig1, timeBasedFragmentCreator,
				ldesFragmentRepository1);

	}

	private TimebasedFragmentationConfig createTimebasedFragmentationConfig(FragmentationProperties properties) {
		TimebasedFragmentationConfig timebasedFragmentationConfig = new TimebasedFragmentationConfig();
		timebasedFragmentationConfig.setMemberLimit(Long.valueOf(properties.get(MEMBER_LIMIT)));
		return timebasedFragmentationConfig;
	}
}
