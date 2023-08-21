package be.vlaanderen.informatievlaanderen.ldes.server.compaction.domain.services;

import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.FragmentationStrategy;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.FragmentationStrategyImpl;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.NonCriticalTasksExecutor;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.FragmentRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
public class SchedulingConfigCompaction {

    @Bean
    @Qualifier("compaction-fragmentation")
    public FragmentationStrategy compactionFragmentationStrategy(FragmentRepository fragmentRepository,
                                                                 NonCriticalTasksExecutor nonCriticalTasksExecutor, ApplicationEventPublisher eventPublisher) {
        return new FragmentationStrategyImpl(fragmentRepository,
                nonCriticalTasksExecutor,
                eventPublisher);

    }

}