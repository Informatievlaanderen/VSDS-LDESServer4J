package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.pagination;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ConfigProperties;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.FragmentationStrategy;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.FragmentationStrategyWrapper;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.FragmentRepository;
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
		FragmentRepository fragmentRepository = applicationContext.getBean(FragmentRepository.class);
		ObservationRegistry observationRegistry = applicationContext.getBean(ObservationRegistry.class);

		OpenPageProvider openFragmentProvider = getOpenPageProvider(fragmentationProperties,
				fragmentRepository);
		return new PaginationStrategy(fragmentationStrategy,
				openFragmentProvider, observationRegistry, fragmentRepository);

	}

	private OpenPageProvider getOpenPageProvider(ConfigProperties properties,
			FragmentRepository fragmentRepository) {
		PaginationConfig paginationConfig = createPaginationConfig(properties);
		PageCreator timeBasedFragmentCreator = getPageCreator(
				fragmentRepository, paginationConfig.bidirectionalRelations());
		return new OpenPageProvider(timeBasedFragmentCreator, fragmentRepository,
				paginationConfig.memberLimit());
	}

	private PageCreator getPageCreator(FragmentRepository fragmentRepository, boolean bidirectionalRelations) {
		return new PageCreator(
				fragmentRepository, bidirectionalRelations);
	}

	private PaginationConfig createPaginationConfig(ConfigProperties properties) {
		return new PaginationConfig(Long.valueOf(properties.get(MEMBER_LIMIT)),
				Boolean.parseBoolean(properties.getOrDefault(BIDIRECTIONAL_RELATIONS, "true")));
	}
}
