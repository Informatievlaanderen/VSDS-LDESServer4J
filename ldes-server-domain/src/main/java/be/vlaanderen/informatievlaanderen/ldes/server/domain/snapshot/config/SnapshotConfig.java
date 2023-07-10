package be.vlaanderen.informatievlaanderen.ldes.server.domain.snapshot.config;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentationStrategy;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentationStrategyImpl;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.NonCriticalTasksExecutor;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.repository.MemberRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.services.FragmentationStrategyWrapper;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ConfigProperties;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.view.service.ViewServiceImpl.DEFAULT_VIEW_FRAGMENTATION_PROPERTIES;
import static be.vlaanderen.informatievlaanderen.ldes.server.domain.view.service.ViewServiceImpl.DEFAULT_VIEW_FRAGMENTATION_STRATEGY;

@Configuration
public class SnapshotConfig {

	@Bean
	@Qualifier("snapshot-fragmentation")
	public FragmentationStrategy snapshotFragmentationStrategy(ApplicationContext applicationContext,
			LdesFragmentRepository ldesFragmentRepository, MemberRepository memberRepository,
			NonCriticalTasksExecutor nonCriticalTasksExecutor, ApplicationEventPublisher eventPublisher) {
		FragmentationStrategyWrapper fragmentationStrategyWrapper = (FragmentationStrategyWrapper) applicationContext
				.getBean(DEFAULT_VIEW_FRAGMENTATION_STRATEGY);
		return fragmentationStrategyWrapper.wrapFragmentationStrategy(
				applicationContext,
				new FragmentationStrategyImpl(ldesFragmentRepository, memberRepository, nonCriticalTasksExecutor,
						eventPublisher),
				new ConfigProperties(DEFAULT_VIEW_FRAGMENTATION_PROPERTIES));

	}
}
