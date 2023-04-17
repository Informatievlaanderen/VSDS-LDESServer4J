package be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentationStrategy;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.AppConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewSpecification;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
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
	@Qualifier("configured-fragmentation")
	public Map<ViewName, FragmentationStrategy> fragmentationStrategyMap(
			FragmentationStrategyCreator fragmentationStrategyCreator,
			AppConfig appConfig) {

		return appConfig
				.getCollections()
				.stream()
				.flatMap(ldesSpec -> ldesSpec.getViews().stream())
				.collect(Collectors.toMap(ViewSpecification::getName,
						fragmentationStrategyCreator::createFragmentationStrategyForView));
	}

}
