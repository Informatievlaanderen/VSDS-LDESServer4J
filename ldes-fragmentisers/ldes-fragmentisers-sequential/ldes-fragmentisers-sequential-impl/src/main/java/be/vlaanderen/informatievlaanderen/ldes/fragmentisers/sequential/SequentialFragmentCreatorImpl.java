package be.vlaanderen.informatievlaanderen.ldes.fragmentisers.sequential;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.PROV_GENERATED_AT_TIME;
import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.TREE_GREATER_THAN_OR_EQUAL_TO_RELATION;
import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.TREE_LESSER_THAN_OR_EQUAL_TO_RELATION;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.config.LdesConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.config.LdesFragmentNamingStrategy;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.SequentialFragmentCreator;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.FragmentInfo;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.TreeRelation;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesmember.entities.LdesMember;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesmember.repository.LdesMemberRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesmember.services.TimestampPathComparator;

@Component
public class SequentialFragmentCreatorImpl implements SequentialFragmentCreator {
    
    protected final LdesConfig ldesConfig;
    protected final SequentialFragmentationConfig sequentialFragmentationConfig;
    protected final LdesFragmentNamingStrategy ldesFragmentNamingStrategy;
    protected final LdesMemberRepository ldesMemberRepository;
    protected final LdesFragmentRepository ldesFragmentRepository;

    public SequentialFragmentCreatorImpl(LdesConfig ldesConfig, SequentialFragmentationConfig timeBasedConfig, LdesFragmentNamingStrategy ldesFragmentNamingStrategy, LdesMemberRepository ldesMemberRepository, LdesFragmentRepository ldesFragmentRepository) {
        this.ldesConfig = ldesConfig;
        this.sequentialFragmentationConfig = timeBasedConfig;
        this.ldesFragmentNamingStrategy = ldesFragmentNamingStrategy;
        this.ldesMemberRepository = ldesMemberRepository;
        this.ldesFragmentRepository = ldesFragmentRepository;
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
        return fragment.getCurrentNumberOfMembers() >= sequentialFragmentationConfig.getMemberLimit();
    }

    protected LdesFragment createNewFragment() {
    	FragmentInfo fragmentInfo = new FragmentInfo(
    			ldesConfig.getHostName() + "/" + ldesConfig.getCollectionName(),
    			ldesConfig.getShape(),
    			ldesConfig.getCollectionName(),
    			List.of(ldesFragmentNamingStrategy.getFragmentationValue()));
        
        return new LdesFragment(ldesFragmentNamingStrategy.generateFragmentName(ldesConfig, fragmentInfo), fragmentInfo);
    }

    protected void makeFragmentImmutableAndUpdateRelations(LdesFragment completeLdesFragment, LdesFragment newFragment) {
        completeLdesFragment.setImmutable(true);
        completeLdesFragment.addRelation(new TreeRelation(newFragment.getFragmentInfo().getPath(), newFragment.getFragmentId(), newFragment.getFragmentInfo().getValue(), TREE_GREATER_THAN_OR_EQUAL_TO_RELATION));
        String latestGeneratedAtTime = getLatestGeneratedAtTime(completeLdesFragment);
        ldesFragmentRepository.saveFragment(completeLdesFragment);
        newFragment.addRelation(new TreeRelation(completeLdesFragment.getFragmentInfo().getPath(), completeLdesFragment.getFragmentId(), latestGeneratedAtTime, TREE_LESSER_THAN_OR_EQUAL_TO_RELATION));
    }

    private String getLatestGeneratedAtTime(LdesFragment completeLdesFragment) {
        return ldesMemberRepository.getLdesMembersByIds(completeLdesFragment.getMemberIds())
                .max(new TimestampPathComparator())
                .map(ldesMember -> ldesMember.getFragmentationValue(PROV_GENERATED_AT_TIME))
                .orElseGet(() -> LocalDateTime.now().format(sequentialFragmentationConfig.getDatetimeFormatter()));
    }
}
