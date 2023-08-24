package be.vlaanderen.informatievlaanderen.ldes.server.compaction.application.services;

import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.FragmentationStrategyImpl;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.FragmentRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FragmentationConfigCompaction {

	@Bean
	@Qualifier("compaction-fragmentation")
	public FragmentationStrategyImpl compactionFragmentationStrategy(FragmentRepository fragmentRepository,
			ApplicationEventPublisher eventPublisher) {
		return new FragmentationStrategyImpl(fragmentRepository,
				eventPublisher);

	}
}
