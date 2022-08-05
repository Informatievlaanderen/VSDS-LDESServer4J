package be.vlaanderen.informatievlaanderen.ldes.fragmentisers.geospatial;

import be.vlaanderen.informatievlaanderen.ldes.fragmentisers.geospatial.bucketising.GeospatialBucketiser;
import be.vlaanderen.informatievlaanderen.ldes.fragmentisers.geospatial.connected.ConnectedFragmentsFinder;
import be.vlaanderen.informatievlaanderen.ldes.fragmentisers.geospatial.fragments.GeospatialFragmentCreator;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.config.LdesFragmentNamingStrategy;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentCreator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.config.LdesConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentationService;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesmember.repository.LdesMemberRepository;

@Configuration
@ConditionalOnProperty(
        value="fragmentation.type",
        havingValue = "geospatial")
@EnableConfigurationProperties()
@ComponentScan("be.vlaanderen.informatievlaanderen.ldes")
public class GeospatialFragmentationServiceAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(FragmentationService.class)
    public FragmentationService geospatialFragmentationService(LdesConfig ldesConfig,
                                                               LdesMemberRepository ldesMemberRepository,
                                                               LdesFragmentRepository ldesFragmentRepository,
                                                               GeospatialBucketiser geospatialBucketiser,
                                                               LdesFragmentNamingStrategy ldesFragmentNamingStrategy,
                                                               ConnectedFragmentsFinder connectedFragmentsFinder) {
        return new GeospatialFragmentationService(ldesConfig, ldesMemberRepository, ldesFragmentRepository, new GeospatialFragmentCreator(ldesConfig, ldesFragmentNamingStrategy), geospatialBucketiser, connectedFragmentsFinder);
    }

    @Bean
    @ConditionalOnMissingBean(FragmentCreator.class)
    public FragmentCreator fragmentCreator(LdesConfig ldesConfig,
                                           LdesFragmentNamingStrategy fragmentNamingStrategy) {
        return new GeospatialFragmentCreator(ldesConfig, fragmentNamingStrategy);
    }

    @Bean
    @ConditionalOnMissingBean(LdesFragmentNamingStrategy.class)
    public LdesFragmentNamingStrategy fragmentNamingStrategy() {
        return new GeospatialFragmentNamingStrategy();
    }

}
