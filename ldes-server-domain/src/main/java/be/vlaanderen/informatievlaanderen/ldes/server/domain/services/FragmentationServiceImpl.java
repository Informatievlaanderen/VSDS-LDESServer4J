package be.vlaanderen.informatievlaanderen.ldes.server.domain.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.config.LdesConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.config.ViewConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.entities.FragmentInfo;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.entities.LdesMember;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.repositories.LdesFragmentRespository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.repositories.LdesMemberRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class FragmentationServiceImpl implements FragmentationService {

    private final LdesConfig ldesConfig;
    private final ViewConfig viewConfig;
    private final LdesMemberRepository ldesMemberRepository;
    private final LdesFragmentRespository ldesFragmentRespository;

    private final FragmentCreator fragmentCreator;

    public FragmentationServiceImpl(LdesConfig ldesConfig, ViewConfig viewConfig,
                                    LdesMemberRepository ldesMemberRepository, LdesFragmentRespository ldesFragmentRespository, FragmentCreator fragmentCreator) {
        this.ldesConfig = ldesConfig;
        this.viewConfig = viewConfig;
        this.ldesMemberRepository = ldesMemberRepository;
        this.ldesFragmentRespository = ldesFragmentRespository;
        this.fragmentCreator = fragmentCreator;
    }

    @Override
    public LdesMember addMember(LdesMember ldesMember) {
        ldesMember.replaceTreeMemberStatement(ldesConfig.getHostName(), ldesConfig.getCollectionName());
        LdesFragment ldesFragment = retrieveLastFragmentOrCreateNewFragment(ldesMember);
        ldesFragment.addMember(ldesMember);
        ldesFragmentRespository.saveFragment(ldesFragment);
        return ldesMemberRepository.saveLdesMember(ldesMember);
    }

    private LdesFragment retrieveLastFragmentOrCreateNewFragment(LdesMember ldesMember) {
        return ldesFragmentRespository.retrieveOpenFragment(ldesConfig.getCollectionName())
                .map(fragment -> {
                    if (fragment.getCurrentNumberOfMembers() >= viewConfig.getMemberLimit()) {
                        return fragmentCreator.createNewFragment(Optional.of(fragment), ldesMember);
                    } else {
                        return fragment;
                    }
                })
                .orElseGet(() -> fragmentCreator.createNewFragment(Optional.empty(), ldesMember));
    }

    @Override
    public LdesFragment getFragment(String viewShortName, String path, String value) {
        return ldesFragmentRespository.retrieveFragment(viewShortName, path, value)
                .orElse(LdesFragment.newFragment(ldesConfig.getHostName(),
                        new FragmentInfo(String.format("%s/%s", ldesConfig.getHostName(), ldesConfig.getCollectionName()), viewConfig.getShape(), viewShortName, path, value)));
    }
}
