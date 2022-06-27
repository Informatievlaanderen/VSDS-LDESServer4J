package be.vlaanderen.informatievlaanderen.ldes.server.domain.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.config.LdesConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.config.ViewConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.entities.FragmentInfo;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.entities.LdesMember;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.entities.TreeRelation;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.repositories.LdesFragmentRespository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.repositories.LdesMemberRepository;
import org.apache.jena.rdf.model.Statement;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.apache.jena.rdf.model.ResourceFactory.createResource;

@Component
public class FragmentationServiceImpl implements FragmentationService {

    private final LdesConfig ldesConfig;
    private final ViewConfig viewConfig;
    private final LdesMemberRepository ldesMemberRepository;
    private final LdesFragmentRespository ldesFragmentRespository;

    public FragmentationServiceImpl(LdesConfig ldesConfig, ViewConfig viewConfig, LdesMemberRepository ldesMemberRepository,
                                    LdesFragmentRespository ldesFragmentRespository) {
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
                    if(fragment.getMembers().size() >= fragment.getFragmentInfo().getMemberLimit()) {
                        return makeNewFollowingFragment(fragment, ldesMember, "tree:GreaterThanRelation", "tree:LesserThanRelation");
                    }
                    else {
                        return fragment;
                    }
                })
                .orElse(makeNewFragment(ldesMember));
        addLdesMembertoLdesFragment(ldesMember, ldesFragment);
        ldesFragmentRespository.saveFragment(ldesFragment);
        return ldesMemberRepository.saveLdesMember(ldesMember);
    }

    private LdesFragment makeNewFollowingFragment(LdesFragment fragment, LdesMember ldesMember, String oldFragmentRelation, String newFragmentRelation) {
        LdesFragment newFragment = makeNewFragment(ldesMember);

        fragment.getFragmentInfo().setImmutable(true);
        fragment.addRelation(new TreeRelation(newFragment, oldFragmentRelation));
        ldesFragmentRespository.saveFragment(fragment);

        newFragment.addRelation(new TreeRelation(fragment, newFragmentRelation));
        return newFragment;
    }

    private void addLdesMembertoLdesFragment(LdesMember ldesMember, LdesFragment ldesFragment) {
        ldesFragment.addMember(ldesMember);
    }

    private LdesFragment makeNewFragment(LdesMember ldesMember) {
        //TODO rework this. should not remove type from timeStampPath

        return LdesFragment.newFragment(ldesConfig.getHostName(),
                new FragmentInfo(String.format("%s/%s", ldesConfig.getHostName(), ldesConfig.getCollectionName()), viewConfig.getShape(), null, ldesConfig.getCollectionName(), viewConfig.getTimestampPath(), viewConfig.getTimestampPathValue(), viewConfig.getMemberLimit()));
    }

    @Override
    public LdesFragment getFragment(String viewShortName, String path, String value) {
        return ldesFragmentRespository.retrieveFragment(viewShortName, path, value)
                .orElse(LdesFragment.newFragment(ldesConfig.getHostName(),
                new FragmentInfo(String.format("%s/%s", ldesConfig.getHostName(), ldesConfig.getCollectionName()), viewConfig.getShape(), null, viewShortName, path, value, viewConfig.getMemberLimit())));
    }
}
