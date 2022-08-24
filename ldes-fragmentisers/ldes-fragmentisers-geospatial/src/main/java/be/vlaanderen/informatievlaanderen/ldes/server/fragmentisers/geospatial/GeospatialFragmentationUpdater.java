package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.config.LdesConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentationService;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentationUpdater;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesmember.repository.LdesMemberRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.bucketising.GeospatialBucketiser;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.connected.ConnectedFragmentsFinder;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.fragments.GeospatialFragmentCreator;
import org.springframework.context.ApplicationContext;

public class GeospatialFragmentationUpdater implements FragmentationUpdater {

    public FragmentationService updateFragmentationService(ApplicationContext applicationContext, FragmentationService fragmentationService) {
        LdesConfig ldesConfig1 = applicationContext.getBean(LdesConfig.class);
        LdesMemberRepository ldesMemberRepository1 = applicationContext.getBean(LdesMemberRepository.class);
        LdesFragmentRepository ldesFragmentRepository1 = applicationContext.getBean(LdesFragmentRepository.class);
        GeospatialBucketiser geospatialBucketiser1 = applicationContext.getBean(GeospatialBucketiser.class);
        ConnectedFragmentsFinder connectedFragmentsFinder1 = applicationContext.getBean(ConnectedFragmentsFinder.class);


        return new GeospatialFragmentationService(fragmentationService, ldesConfig1, ldesMemberRepository1, ldesFragmentRepository1,
                new GeospatialFragmentCreator(ldesConfig1), geospatialBucketiser1,
                connectedFragmentsFinder1);
    }
}
