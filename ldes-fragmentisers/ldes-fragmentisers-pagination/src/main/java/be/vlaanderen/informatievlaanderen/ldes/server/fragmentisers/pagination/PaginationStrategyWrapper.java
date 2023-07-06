package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.pagination;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ConfigProperties;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.FragmentationStrategy;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.FragmentationStrategyWrapper;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.pagination.config.PaginationConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.pagination.services.OpenPageProvider;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.pagination.services.PageCreator;
import io.micrometer.observation.ObservationRegistry;
import org.springframework.context.ApplicationContext;

import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.pagination.config.PaginationProperties.BIDIRECTIONAL_RELATIONS;
import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.pagination.config.PaginationProperties.MEMBER_LIMIT;

public class PaginationStrategyWrapper implements FragmentationStrategyWrapper {

	public FragmentationStrategy wrapFragmentationStrategy(ApplicationContext applicationContext,
			FragmentationStrategy fragmentationStrategy, ConfigProperties fragmentationProperties) {
		LdesFragmentRepository ldesFragmentRepository = applicationContext.getBean(LdesFragmentRepository.class);
		ObservationRegistry observationRegistry = applicationContext.getBean(ObservationRegistry.class);

		OpenPageProvider openFragmentProvider = getOpenPageProvider(fragmentationProperties,
				ldesFragmentRepository);
		return new PaginationStrategy(fragmentationStrategy,
				openFragmentProvider, observationRegistry, ldesFragmentRepository);

	}

	private OpenPageProvider getOpenPageProvider(ConfigProperties properties,
			LdesFragmentRepository ldesFragmentRepository) {
		PaginationConfig paginationConfig = createPaginationConfig(properties);
		PageCreator timeBasedFragmentCreator = getPageCreator(
				ldesFragmentRepository, paginationConfig.bidirectionalRelations());
		return new OpenPageProvider(timeBasedFragmentCreator, ldesFragmentRepository,
				paginationConfig.memberLimit());
	}

	private PageCreator getPageCreator(LdesFragmentRepository ldesFragmentRepository, boolean bidirectionalRelations) {
		return new PageCreator(
				ldesFragmentRepository, bidirectionalRelations);
	}

	private PaginationConfig createPaginationConfig(ConfigProperties properties) {
		return new PaginationConfig(Long.valueOf(properties.get(MEMBER_LIMIT)),
				Boolean.parseBoolean(properties.getOrDefault(BIDIRECTIONAL_RELATIONS, "true")));
	}
}
