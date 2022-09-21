package be.vlaanderen.informatievlaanderen.ldes.server.domain.config;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentationService;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentationServiceImpl;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentationUpdater;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.stream.Collectors;

@Configuration
@EnableConfigurationProperties()
@ComponentScan("be.vlaanderen.informatievlaanderen.ldes.server")
public class FragmentationStrategyConfig {

	@Bean
	public Map<String, FragmentationService> fragmentationService(ApplicationContext applicationContext,
			ViewConfig viewConfig) {

		return viewConfig
				.getViews()
				.stream()
				.collect(Collectors.toMap(ViewSpecification::getName,
						viewSpecification -> getFragmentationService(applicationContext, viewSpecification)));
	}

	private FragmentationService getFragmentationService(ApplicationContext applicationContext,
			ViewSpecification viewSpecification) {
		FragmentationService fragmentationService = getFragmentationServiceImpl(applicationContext,
				viewSpecification.getName());
		if (viewSpecification.getFragmentations() != null)
			for (int i = viewSpecification.getFragmentations().size() - 1; i >= 0; i--) {
				String fragmentation = viewSpecification.getFragmentations().get(i).getName();
				FragmentationUpdater fragmentationUpdater = (FragmentationUpdater) applicationContext
						.getBean(fragmentation);
				fragmentationService = fragmentationUpdater.updateFragmentationService(applicationContext,
						fragmentationService, viewSpecification.getFragmentations().get(i).getProperties());
			}
		return fragmentationService;
	}

	private FragmentationService getFragmentationServiceImpl(ApplicationContext applicationContext, String name) {
		LdesFragmentRepository ldesFragmentRepository = applicationContext.getBean(LdesFragmentRepository.class);
		LdesConfig ldesConfig = applicationContext.getBean(LdesConfig.class);
		Tracer tracer = applicationContext.getBean(Tracer.class);

		return new FragmentationServiceImpl(ldesFragmentRepository,
				ldesConfig, name, tracer);
	}

}
