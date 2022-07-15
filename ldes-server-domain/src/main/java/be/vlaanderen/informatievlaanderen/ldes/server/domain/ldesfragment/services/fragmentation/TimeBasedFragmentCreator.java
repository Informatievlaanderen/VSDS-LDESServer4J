package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.fragmentation;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.config.LdesConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.config.ViewConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.FragmentInfo;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.TreeRelation;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.exceptions.LdesMemberNotFoundException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRespository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.entities.FragmentPair;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesmember.entities.LdesMember;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesmember.repository.LdesMemberRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesmember.services.TimestampPathComparator;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Component
@Qualifier("default")
public class TimeBasedFragmentCreator implements FragmentCreator {
    private static final String TREE_GREATER_THAN_OR_EQUAL_TO_RELATION = "tree:GreaterThanOrEqualToRelation";
    private static final String TREE_LESSER_THAN_OR_EQUAL_TO_RELATION = "tree:LessThanOrEqualToRelation";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    private final LdesConfig ldesConfig;
    private final ViewConfig viewConfig;
    private final LdesFragmentRespository ldesFragmentRespository;

    private final LdesMemberRepository ldesMemberRepository;

    public TimeBasedFragmentCreator(LdesConfig ldesConfig, ViewConfig viewConfig, LdesFragmentRespository ldesFragmentRespository, LdesMemberRepository ldesMemberRepository) {
        this.ldesConfig = ldesConfig;
        this.viewConfig = viewConfig;
        this.ldesFragmentRespository = ldesFragmentRespository;
        this.ldesMemberRepository = ldesMemberRepository;
    }

    @Override
    public LdesFragment createNewFragment(Optional<LdesFragment> optionalLdesFragment, LdesMember firstMember) {
        LdesFragment newFragment = createNewFragment();
        optionalLdesFragment
                .ifPresent(ldesFragment -> makeFragmentImmutableAndUpdateRelations(ldesFragment, newFragment));
        return newFragment;
    }

    private void makeFragmentImmutableAndUpdateRelations(LdesFragment completeLdesFragment, LdesFragment newFragment) {
        completeLdesFragment.setImmutable(true);
        completeLdesFragment.addRelation(new TreeRelation(newFragment.getFragmentInfo().getPath(), newFragment.getFragmentId(), newFragment.getFragmentInfo().getValue(), TREE_GREATER_THAN_OR_EQUAL_TO_RELATION));
        String latestGeneratedAtTime = getLatestGeneratedAtTime(completeLdesFragment);
        ldesFragmentRespository.saveFragment(completeLdesFragment);
        newFragment.addRelation(new TreeRelation(completeLdesFragment.getFragmentInfo().getPath(), completeLdesFragment.getFragmentId(), latestGeneratedAtTime, TREE_LESSER_THAN_OR_EQUAL_TO_RELATION));
    }

    private String getLatestGeneratedAtTime(LdesFragment completeLdesFragment) {
        return completeLdesFragment
                .getMemberIds()
                .stream()
                .map(memberId->ldesMemberRepository
                        .getLdesMemberById(memberId)
                        .orElseThrow(()->new LdesMemberNotFoundException(memberId)))
                .max(new TimestampPathComparator())
                .map(ldesMember -> ldesMember.getFragmentationValue(viewConfig.getTimestampPath()))
                .orElseGet(() -> LocalDateTime.now().format(formatter));
    }

    private LdesFragment createNewFragment() {
        String fragmentationValue = LocalDateTime.now().format(formatter);
        return LdesFragment.newFragment(ldesConfig.getHostName(),
                new FragmentInfo(String.format("%s/%s", ldesConfig.getHostName(), ldesConfig.getCollectionName()), viewConfig.getShape(), ldesConfig.getCollectionName(), List.of(new FragmentPair(viewConfig.getCompactTimestampPath(), fragmentationValue))));
    }
}
