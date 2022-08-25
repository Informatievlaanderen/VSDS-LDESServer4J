package be.vlaanderen.informatievlaanderen.ldes.server.domain.config;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentationService;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentationServiceImpl;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentationUpdater;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesmember.repository.LdesMemberRepository;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@EnableConfigurationProperties()
@ComponentScan("be.vlaanderen.informatievlaanderen.ldes.server")
public class FragmentationConfig {

    @Bean
    public FragmentationService fragmentationService(ApplicationContext applicationContext,
                                                     FragmentConfig fragmentConfig) {
        LdesMemberRepository ldesMemberRepository = applicationContext.getBean(LdesMemberRepository.class);
        LdesFragmentRepository ldesFragmentRepository = applicationContext.getBean(LdesFragmentRepository.class);
        LdesConfig ldesConfig = applicationContext.getBean(LdesConfig.class);
        FragmentationService fragmentationService = new FragmentationServiceImpl(ldesFragmentRepository,
                ldesMemberRepository, ldesConfig);

        List<String> fragmentations = fragmentConfig.getFragmentations();
        if (fragmentations != null) {
            for (int i = fragmentations.size() - 1; i >= 0; i--) {
                String fragmentation = fragmentations.get(i);
                FragmentationUpdater fragmentationUpdater = (FragmentationUpdater) applicationContext
                        .getBean(fragmentation);
                fragmentationService = fragmentationUpdater.updateFragmentationService(applicationContext,
                        fragmentationService);
            }
        }
        return fragmentationService;
    }
}
