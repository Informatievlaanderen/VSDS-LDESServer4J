package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebased;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.config.LdesConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentationService;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentationUpdater;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesmember.repository.LdesMemberRepository;
import org.springframework.context.ApplicationContext;

public class TimebasedFragmentationUpdater implements FragmentationUpdater {

	public FragmentationService updateFragmentationService(ApplicationContext applicationContext,
			FragmentationService fragmentationService) {
		LdesConfig ldesConfig1 = applicationContext.getBean(LdesConfig.class);
		LdesMemberRepository ldesMemberRepository1 = applicationContext.getBean(LdesMemberRepository.class);
		LdesFragmentRepository ldesFragmentRepository1 = applicationContext.getBean(LdesFragmentRepository.class);
		SequentialFragmentationConfig sequentialFragmentationConfig = applicationContext
				.getBean(SequentialFragmentationConfig.class);

		TimeBasedFragmentCreator timeBasedFragmentCreator = new TimeBasedFragmentCreator(ldesConfig1,
				sequentialFragmentationConfig,
				ldesMemberRepository1, ldesFragmentRepository1);
		return new TimebasedFragmentationService(fragmentationService, ldesConfig1, timeBasedFragmentCreator,
				ldesFragmentRepository1);

	}
}
