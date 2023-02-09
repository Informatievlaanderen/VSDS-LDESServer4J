package be.vlaanderen.informatievlaanderen.vsds;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentationStrategy;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.NonCriticalTasksExecutor;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.relations.TreeRelationsRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.services.FragmentationStrategyWrapper;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ConfigProperties;
import be.vlaanderen.informatievlaanderen.vsds.config.PaginationConfig;
import be.vlaanderen.informatievlaanderen.vsds.services.OpenPageProvider;
import be.vlaanderen.informatievlaanderen.vsds.services.PageCreator;
import io.micrometer.observation.ObservationRegistry;
import org.springframework.context.ApplicationContext;

import static be.vlaanderen.informatievlaanderen.vsds.config.PaginationProperties.MEMBER_LIMIT;

public class PaginationStrategyWrapper implements FragmentationStrategyWrapper {

	public FragmentationStrategy wrapFragmentationStrategy(ApplicationContext applicationContext,
			FragmentationStrategy fragmentationStrategy, ConfigProperties fragmentationProperties) {
		LdesFragmentRepository ldesFragmentRepository = applicationContext.getBean(LdesFragmentRepository.class);
		ObservationRegistry observationRegistry = applicationContext.getBean(ObservationRegistry.class);
		TreeRelationsRepository treeRelationsRepository = applicationContext
				.getBean(TreeRelationsRepository.class);
		NonCriticalTasksExecutor nonCriticalTasksExecutor = applicationContext.getBean(NonCriticalTasksExecutor.class);

		OpenPageProvider openFragmentProvider = getOpenPageProvider(fragmentationProperties,
				ldesFragmentRepository, treeRelationsRepository, nonCriticalTasksExecutor);
		return new PaginationStrategy(fragmentationStrategy,
				openFragmentProvider, observationRegistry, treeRelationsRepository);

	}

	private OpenPageProvider getOpenPageProvider(ConfigProperties properties,
			LdesFragmentRepository ldesFragmentRepository, TreeRelationsRepository treeRelationsRepository,
			NonCriticalTasksExecutor nonCriticalTasksExecutor) {
		PaginationConfig paginationConfig = createPaginationConfigConfig(properties);
		PageCreator timeBasedFragmentCreator = getPageCreator(
				ldesFragmentRepository, treeRelationsRepository, nonCriticalTasksExecutor);
		return new OpenPageProvider(timeBasedFragmentCreator, ldesFragmentRepository,
				paginationConfig.memberLimit());
	}

	private PageCreator getPageCreator(LdesFragmentRepository ldesFragmentRepository,
			TreeRelationsRepository treeRelationsRepository,
			NonCriticalTasksExecutor nonCriticalTasksExecutor) {
		return new PageCreator(
				ldesFragmentRepository, treeRelationsRepository, nonCriticalTasksExecutor);
	}

	private PaginationConfig createPaginationConfigConfig(ConfigProperties properties) {
		return new PaginationConfig(Long.valueOf(properties.get(MEMBER_LIMIT)));
	}
}
