package be.vlaanderen.informatievlaanderen.ldes.fragmentisers.timebased;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.PROV_GENERATED_AT_TIME;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import be.vlaanderen.informatievlaanderen.ldes.fragmentisers.sequential.SequentialFragmentCreatorImpl;
import be.vlaanderen.informatievlaanderen.ldes.fragmentisers.sequential.SequentialFragmentationConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.config.LdesConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.config.LdesFragmentNamingStrategy;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesmember.entities.LdesMember;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesmember.repository.LdesMemberRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesmember.services.TimestampPathComparator;

@Component
@Qualifier("default")
public class TimeBasedFragmentCreator extends SequentialFragmentCreatorImpl {

    public TimeBasedFragmentCreator(LdesConfig ldesConfig, SequentialFragmentationConfig sequentialFragmentationConfig, LdesFragmentNamingStrategy ldesFragmentNamingStrategy, LdesMemberRepository ldesMemberRepository, LdesFragmentRepository ldesFragmentRepository) {
    	super(ldesConfig, sequentialFragmentationConfig, ldesFragmentNamingStrategy, ldesMemberRepository, ldesFragmentRepository);
    }

    @Override
    public LdesFragment createNewFragment(Optional<LdesFragment> optionalLdesFragment, LdesMember firstMember) {
        LdesFragment newFragment = createNewFragment();
        optionalLdesFragment
                .ifPresent(ldesFragment -> makeFragmentImmutableAndUpdateRelations(ldesFragment, newFragment));
        return newFragment;
    }
    
    private String getLatestGeneratedAtTime(LdesFragment completeLdesFragment) {
        return ldesMemberRepository.getLdesMembersByIds(completeLdesFragment.getMemberIds())
                .max(new TimestampPathComparator())
                .map(ldesMember -> ldesMember.getFragmentationValue(PROV_GENERATED_AT_TIME))
                .orElseGet(() -> LocalDateTime.now().format(sequentialFragmentationConfig.getDatetimeFormatter()));
    }
}
