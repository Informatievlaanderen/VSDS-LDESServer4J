package be.vlaanderen.informatievlaanderen.ldes.server.domain.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.config.LdesConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.config.ViewConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.entities.FragmentInfo;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.entities.LdesMember;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.entities.TreeRelation;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.repositories.LdesFragmentRespository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Qualifier("default")
public class TimeBasedFragmentCreator implements FragmentCreator {
    private static final String TREE_GREATER_THAN_RELATION = "tree:GreaterThanRelation";
    private static final String TREE_LESSER_THAN_RELATION = "tree:LesserThanRelation";

    private final LdesConfig ldesConfig;
    private final ViewConfig viewConfig;
    private final LdesFragmentRespository ldesFragmentRespository;

    public TimeBasedFragmentCreator(LdesConfig ldesConfig, ViewConfig viewConfig, LdesFragmentRespository ldesFragmentRespository) {
        this.ldesConfig = ldesConfig;
        this.viewConfig = viewConfig;
        this.ldesFragmentRespository = ldesFragmentRespository;
    }

    @Override
    public LdesFragment createNewFragment(Optional<LdesFragment> optionalLdesFragment, LdesMember firstMember) {
        LdesFragment newFragment = createFreshFragment(firstMember);
        optionalLdesFragment
                .ifPresent(ldesFragment -> makeFragmentImmutableAndUpdateRelations(ldesFragment, newFragment));
        return newFragment;
    }

    private void makeFragmentImmutableAndUpdateRelations(LdesFragment completeLdesFragment, LdesFragment newFragment) {
        completeLdesFragment.setImmutable(true);
        completeLdesFragment.addRelation(new TreeRelation(newFragment.getFragmentInfo().getPath(), newFragment.getFragmentId(), newFragment.getFragmentInfo().getValue(), TREE_GREATER_THAN_RELATION));
        ldesFragmentRespository.saveFragment(completeLdesFragment);
        newFragment.addRelation(new TreeRelation(completeLdesFragment.getFragmentInfo().getPath(), completeLdesFragment.getFragmentId(), completeLdesFragment.getFragmentInfo().getValue(), TREE_LESSER_THAN_RELATION));
    }

    private LdesFragment createFreshFragment(LdesMember firstMember) {
        String fragmentationValue = firstMember.getFragmentationValue(viewConfig.getTimestampPath());
        return LdesFragment.newFragment(ldesConfig.getHostName(),
                new FragmentInfo(String.format("%s/%s", ldesConfig.getHostName(), ldesConfig.getCollectionName()), viewConfig.getShape(), ldesConfig.getCollectionName(), viewConfig.getTimestampPath(), fragmentationValue));
    }
}
