package be.vlaanderen.informatievlaanderen.ldes.server.domain.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.config.LdesConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.config.ViewConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.entities.FragmentInfo;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.entities.LdesMember;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.entities.TreeRelation;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.repositories.LdesFragmentRespository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.repositories.LdesMemberRepository;
import org.springframework.stereotype.Component;

@Component
public class FragmentationServiceImpl implements FragmentationService {

    private final LdesConfig ldesConfig;
    private final ViewConfig viewConfig;
    private final LdesMemberRepository ldesMemberRepository;
    private final LdesFragmentRespository ldesFragmentRespository;

    public FragmentationServiceImpl(LdesConfig ldesConfig, ViewConfig viewConfig,
                                    LdesMemberRepository ldesMemberRepository, LdesFragmentRespository ldesFragmentRespository) {
        this.ldesConfig = ldesConfig;
        this.viewConfig = viewConfig;
        this.ldesMemberRepository = ldesMemberRepository;
        this.ldesFragmentRespository = ldesFragmentRespository;
    }

    @Override
    public LdesMember addMember(LdesMember ldesMember) {
        ldesMember.resetLdesMemberView(ldesConfig);
        LdesFragment ldesFragment = ldesFragmentRespository.retrieveLastFragment(ldesConfig.getCollectionName())
                .map(fragment -> {
                    if (fragment.getMembers().size() >= fragment.getFragmentInfo().getMemberLimit()) {
                        return makeNewFollowingFragment(fragment, ldesMember, "tree:GreaterThanRelation", "tree:LesserThanRelation");
                    } else {
                        fragment.addMember(ldesMember);
                        return fragment;
                    }
                }).orElse(makeNewFragment(ldesMember));
        ldesFragmentRespository.saveFragment(ldesFragment);
        return ldesMemberRepository.saveLdesMember(ldesMember);
    }

    private LdesFragment makeNewFollowingFragment(LdesFragment fragment,
                                                  LdesMember ldesMember,
                                                  String oldFragmentRelation,
                                                  String newFragmentRelation) {
        LdesFragment newFragment = makeNewFragment(ldesMember);

        fragment.getFragmentInfo().setImmutable(true);
        fragment.addRelation(new TreeRelation(newFragment, oldFragmentRelation, viewConfig.getTimestampPath()));
        ldesFragmentRespository.saveFragment(fragment);

        newFragment.addRelation(new TreeRelation(fragment, newFragmentRelation, viewConfig.getTimestampPath()));
        return newFragment;
    }

    private LdesFragment makeNewFragment(LdesMember ldesMember) {
        LdesFragment ldesFragment = LdesFragment.newFragment(ldesConfig.getHostName(),
                new FragmentInfo(String.format("%s/%s", ldesConfig.getHostName(), ldesConfig.getCollectionName()),
                        viewConfig.getShape(), null, ldesConfig.getCollectionName(), viewConfig.getTimestampPath(),
                        ldesMember.getFragmentationValue(viewConfig.getTimestampPath()), viewConfig.getMemberLimit()));
        ldesFragment.addMember(ldesMember);
        return ldesFragment;
    }

    @Override
    public LdesFragment getFragment(String viewShortName, String path, String value) {
        return ldesFragmentRespository.retrieveFragment(viewShortName, path, value)
                .orElse(LdesFragment.newFragment(ldesConfig.getHostName(),
                        new FragmentInfo(String.format("%s/%s", ldesConfig.getHostName(), ldesConfig.getCollectionName()), viewConfig.getShape(), null, viewShortName, path, value, viewConfig.getMemberLimit())));
    }
}
