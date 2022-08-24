package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.hierarchy;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.config.LdesConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentationService;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentationUpdater;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesmember.repository.LdesMemberRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.hierarchy.bucketising.HierarchyGeospatialBucketiser;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.hierarchy.fragments.GeospatialFragmentCreator;
import org.springframework.context.ApplicationContext;

public class HierarchyGeospatialFragmentationUpdater implements FragmentationUpdater {

    public FragmentationService updateFragmentationService(ApplicationContext applicationContext, FragmentationService fragmentationService) {
        LdesConfig ldesConfig1 = applicationContext.getBean(LdesConfig.class);
        LdesMemberRepository ldesMemberRepository1 = applicationContext.getBean(LdesMemberRepository.class);
        LdesFragmentRepository ldesFragmentRepository1 = applicationContext.getBean(LdesFragmentRepository.class);
        HierarchyGeospatialBucketiser geospatialBucketiser1 = applicationContext.getBean(HierarchyGeospatialBucketiser.class);


        return new GeospatialFragmentationService(fragmentationService, ldesConfig1, ldesMemberRepository1, ldesFragmentRepository1,
                new GeospatialFragmentCreator(ldesConfig1), geospatialBucketiser1);
    }
}
