package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentview.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.config.LdesConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.FragmentIdConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentationService;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.entities.LdesFragmentRequest;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentview.entities.LdesFragmentView;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentview.repository.LdesFragmentViewRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FragmentViewingServiceImpl implements FragmentViewingService {

    private final LdesConfig ldesConfig;
    private final LdesFragmentViewRepository ldesFragmentViewRepository;
    private final FragmentationService fragmentationService;
    private final LdesFragmentViewConverter ldesFragmentViewConverter;

    public FragmentViewingServiceImpl(LdesConfig ldesConfig,
                                      LdesFragmentViewRepository ldesFragmentViewRepository, FragmentationService fragmentationService, LdesFragmentViewConverter ldesFragmentViewConverter) {
        this.ldesConfig = ldesConfig;
        this.ldesFragmentViewRepository = ldesFragmentViewRepository;
        this.fragmentationService = fragmentationService;
        this.ldesFragmentViewConverter = ldesFragmentViewConverter;
    }

    @Override
    public LdesFragmentView getFragment(LdesFragmentRequest ldesFragmentRequest) {
        String fragmentId = FragmentIdConverter.toFragmentId(ldesConfig.getHostName(), ldesFragmentRequest.collectionName(), ldesFragmentRequest.fragmentPairs());
        return ldesFragmentViewRepository.getFragmentViewById(fragmentId)
                .orElseGet(()->ldesFragmentViewConverter.convertToLdesFragmentView(fragmentationService.getFragment(ldesFragmentRequest)));
       }


    @Override
    public LdesFragmentView getInitialFragment(String collectionName) {
       return ldesFragmentViewRepository.getInitialFragment()
                .orElseGet(()-> ldesFragmentViewConverter.convertToLdesFragmentView(fragmentationService.getInitialFragment(new LdesFragmentRequest(collectionName, List.of()))));

    }

    @Override
    public void saveImmutableLdesFragment(LdesFragment ldesFragment) {
        ldesFragmentViewRepository.saveLdesFragmentView(ldesFragmentViewConverter.convertToLdesFragmentView(ldesFragment));
    }
}
