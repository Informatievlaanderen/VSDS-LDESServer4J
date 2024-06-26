package be.vlaanderen.informatievlaanderen.ldes.server.compaction.application.services;

import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.FragmentationStrategy;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.FragmentationStrategyImpl;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.FragmentRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FragmentationConfigCompaction {

	@Bean("compactionFragmentation")
	public FragmentationStrategy compactionFragmentationStrategy(FragmentRepository fragmentRepository,
			ApplicationEventPublisher eventPublisher) {
		return new FragmentationStrategyImpl();
	}
}
