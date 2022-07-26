package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.timebasedfragmentation;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.config.LdesConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.FragmentInfo;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.TreeRelation;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRespository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentCreator;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.entities.FragmentPair;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentview.services.FragmentViewingService;
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
    private static final String GENERATED_AT_TIME = "generatedAtTime";
    private static final String PROV_GENERATED_AT_TIME = "http://www.w3.org/ns/prov#"+GENERATED_AT_TIME;
    private final LdesConfig ldesConfig;
    private final TimeBasedConfig timeBasedConfig;
    private final LdesFragmentRespository ldesFragmentRespository;
    private final LdesMemberRepository ldesMemberRepository;
    private final FragmentViewingService fragmentViewingService;

    public TimeBasedFragmentCreator(LdesConfig ldesConfig, TimeBasedConfig timeBasedConfig, LdesFragmentRespository ldesFragmentRespository, LdesMemberRepository ldesMemberRepository, FragmentViewingService fragmentViewingService) {
        this.ldesConfig = ldesConfig;
        this.timeBasedConfig = timeBasedConfig;
        this.ldesFragmentRespository = ldesFragmentRespository;
        this.ldesMemberRepository = ldesMemberRepository;
        this.fragmentViewingService = fragmentViewingService;
    }

    @Override
    public LdesFragment createNewFragment(Optional<LdesFragment> optionalLdesFragment, LdesMember firstMember) {
        LdesFragment newFragment = createNewFragment();
        optionalLdesFragment
                .ifPresent(ldesFragment -> makeFragmentImmutableAndUpdateRelations(ldesFragment, newFragment));
        return newFragment;
    }

    @Override
    public boolean needsToCreateNewFragment(LdesFragment fragment) {
        return fragment.getCurrentNumberOfMembers() >= timeBasedConfig.getMemberLimit();
    }

    private void makeFragmentImmutableAndUpdateRelations(LdesFragment completeLdesFragment, LdesFragment newFragment) {
        completeLdesFragment.addRelation(new TreeRelation(newFragment.getFragmentInfo().getPath(), newFragment.getFragmentId(), newFragment.getFragmentInfo().getValue(), TREE_GREATER_THAN_OR_EQUAL_TO_RELATION));
        String latestGeneratedAtTime = getLatestGeneratedAtTime(completeLdesFragment);
        completeLdesFragment.setImmutable(true);
        ldesFragmentRespository.saveFragment(completeLdesFragment);
        fragmentViewingService.saveImmutableLdesFragment(completeLdesFragment);
        newFragment.addRelation(new TreeRelation(completeLdesFragment.getFragmentInfo().getPath(), completeLdesFragment.getFragmentId(), latestGeneratedAtTime, TREE_LESSER_THAN_OR_EQUAL_TO_RELATION));
    }

    private String getLatestGeneratedAtTime(LdesFragment completeLdesFragment) {
        return ldesMemberRepository.getLdesMembersByIds(completeLdesFragment.getMemberIds())
                .max(new TimestampPathComparator())
                .map(ldesMember -> ldesMember.getFragmentationValue(PROV_GENERATED_AT_TIME))
                .orElseGet(() -> LocalDateTime.now().format(formatter));
    }

    private LdesFragment createNewFragment() {
        String fragmentationValue = LocalDateTime.now().format(formatter);
        return LdesFragment.newFragment(ldesConfig.getHostName(),
                new FragmentInfo(ldesConfig.getHostName() + "/" + ldesConfig.getCollectionName(), ldesConfig.getShape(), ldesConfig.getCollectionName(), List.of(new FragmentPair(GENERATED_AT_TIME, fragmentationValue))));
    }
}
