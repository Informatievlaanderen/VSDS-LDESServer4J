package be.vlaanderen.informatievlaanderen.ldes.fragmentisers.sequential;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.config.LdesConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.FragmentInfo;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.TreeRelation;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentCreator;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.entities.FragmentPair;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesmember.entities.LdesMember;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesmember.repository.LdesMemberRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesmember.services.TimestampPathComparator;

@Component
public class SequentialFragmentCreator implements FragmentCreator {
	
    private static final String TREE_GREATER_THAN_OR_EQUAL_TO_RELATION = "tree:GreaterThanOrEqualToRelation";
    private static final String TREE_LESSER_THAN_OR_EQUAL_TO_RELATION = "tree:LessThanOrEqualToRelation";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    private static final String GENERATED_AT_TIME = "generatedAtTime";
    private static final String PROV_GENERATED_AT_TIME = "http://www.w3.org/ns/prov#"+GENERATED_AT_TIME;
    private final LdesConfig ldesConfig;
    private final SequentialFragmentationConfig timeBasedConfig;
    private final LdesMemberRepository ldesMemberRepository;
    private final LdesFragmentRepository ldesFragmentRespository;

    public SequentialFragmentCreator(LdesConfig ldesConfig, SequentialFragmentationConfig timeBasedConfig, LdesMemberRepository ldesMemberRepository, LdesFragmentRepository ldesFragmentRepository) {
        this.ldesConfig = ldesConfig;
        this.timeBasedConfig = timeBasedConfig;
        this.ldesMemberRepository = ldesMemberRepository;
        this.ldesFragmentRespository = ldesFragmentRepository;
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

    private LdesFragment createNewFragment() {
        String fragmentationValue = LocalDateTime.now().format(formatter);
        return LdesFragment.newFragment(ldesConfig.getHostName(),
                new FragmentInfo(ldesConfig.getHostName() + "/" + ldesConfig.getCollectionName(), ldesConfig.getShape(), ldesConfig.getCollectionName(), List.of(new FragmentPair(GENERATED_AT_TIME, fragmentationValue))));
    }

    private void makeFragmentImmutableAndUpdateRelations(LdesFragment completeLdesFragment, LdesFragment newFragment) {
        completeLdesFragment.setImmutable(true);
        completeLdesFragment.addRelation(new TreeRelation(newFragment.getFragmentInfo().getPath(), newFragment.getFragmentId(), newFragment.getFragmentInfo().getValue(), TREE_GREATER_THAN_OR_EQUAL_TO_RELATION));
        String latestGeneratedAtTime = getLatestGeneratedAtTime(completeLdesFragment);
        ldesFragmentRespository.saveFragment(completeLdesFragment);
        newFragment.addRelation(new TreeRelation(completeLdesFragment.getFragmentInfo().getPath(), completeLdesFragment.getFragmentId(), latestGeneratedAtTime, TREE_LESSER_THAN_OR_EQUAL_TO_RELATION));
    }

    private String getLatestGeneratedAtTime(LdesFragment completeLdesFragment) {
        return ldesMemberRepository.getLdesMembersByIds(completeLdesFragment.getMemberIds())
                .max(new TimestampPathComparator())
                .map(ldesMember -> ldesMember.getFragmentationValue(PROV_GENERATED_AT_TIME))
                .orElseGet(() -> LocalDateTime.now().format(formatter));
    }
}
