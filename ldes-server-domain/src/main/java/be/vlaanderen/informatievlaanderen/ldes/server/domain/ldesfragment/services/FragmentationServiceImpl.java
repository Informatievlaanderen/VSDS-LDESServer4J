package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.config.LdesConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.config.ViewConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.FragmentInfo;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRespository;
import org.springframework.stereotype.Component;

@Component
public class FragmentationServiceImpl implements FragmentationService {

    private final LdesConfig ldesConfig;
    private final ViewConfig viewConfig;
    private final LdesFragmentRespository ldesFragmentRespository;

    public FragmentationServiceImpl(LdesConfig ldesConfig, ViewConfig viewConfig,
                                    LdesFragmentRespository ldesFragmentRespository) {
        this.ldesConfig = ldesConfig;
        this.viewConfig = viewConfig;
        this.ldesFragmentRespository = ldesFragmentRespository;
    }

    @Override
    public LdesFragment getFragment(String collectionName, String path, String value) {
        return ldesFragmentRespository.retrieveFragment(collectionName, path, value)
                .orElseGet(()-> createDummyFragment(collectionName, path));
    }

    @Override
    public LdesFragment getInitialFragment(String collectionName, String path) {
       return ldesFragmentRespository.retrieveInitialFragment(collectionName)
                .orElseGet(()-> createDummyFragment(collectionName, path));

    }

    private LdesFragment createDummyFragment(String collectionName, String timestampPath) {
        return LdesFragment.newFragment(ldesConfig.getHostName(),
                new FragmentInfo(String.format("%s/%s", ldesConfig.getHostName(), ldesConfig.getCollectionName()), viewConfig.getShape(), collectionName, timestampPath, null));
    }
}
