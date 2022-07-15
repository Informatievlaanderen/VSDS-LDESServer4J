package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.config.LdesConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.config.ViewConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.FragmentInfo;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRespository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.entities.FragmentPair;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.entities.LdesFragmentRequest;
import org.springframework.stereotype.Component;

import java.util.List;

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
    public LdesFragment getFragment(LdesFragmentRequest ldesFragmentRequest) {
        return ldesFragmentRespository.retrieveFragment(ldesFragmentRequest)
                .orElseGet(()-> createEmptyFragment(ldesFragmentRequest.collectionName(), ldesFragmentRequest.fragmentPairs()));
    }

    @Override
    public LdesFragment getInitialFragment(LdesFragmentRequest ldesFragmentRequest) {
       return ldesFragmentRespository.retrieveInitialFragment(ldesFragmentRequest.collectionName())
                .orElseGet(()-> createEmptyFragment(ldesFragmentRequest.collectionName(), ldesFragmentRequest.fragmentPairs()));

    }

    private LdesFragment createEmptyFragment(String collectionName, List<FragmentPair> fragmentationMap) {
        return LdesFragment.newFragment(ldesConfig.getHostName(),
                new FragmentInfo(String.format("%s/%s", ldesConfig.getHostName(), ldesConfig.getCollectionName()), viewConfig.getShape(), collectionName, fragmentationMap));
    }
}
