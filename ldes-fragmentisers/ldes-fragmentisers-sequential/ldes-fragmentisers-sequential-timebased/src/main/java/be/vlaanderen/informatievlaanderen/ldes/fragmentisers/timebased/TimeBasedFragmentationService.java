package be.vlaanderen.informatievlaanderen.ldes.fragmentisers.timebased;

import be.vlaanderen.informatievlaanderen.ldes.fragmentisers.sequential.SequentialFragmentationService;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.config.LdesConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.MemberNotFoundException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentCreator;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesmember.entities.LdesMember;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesmember.repository.LdesMemberRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;


@Component
public class TimeBasedFragmentationService extends SequentialFragmentationService {

    public TimeBasedFragmentationService(LdesConfig ldesConfig, FragmentCreator fragmentCreator, LdesMemberRepository ldesMemberRepository, LdesFragmentRepository ldesFragmentRepository) {
        super(ldesConfig, fragmentCreator, ldesMemberRepository, ldesFragmentRepository);
    }

    public void addMemberToFragment(String ldesMemberId) {
        LdesMember ldesMember = ldesMemberRepository.getLdesMemberById(ldesMemberId).orElseThrow(() -> new MemberNotFoundException(ldesMemberId));
        LdesFragment ldesFragment = retrieveLastFragmentOrCreateNewFragment(ldesMember);
        ldesFragment.addMember(ldesMember.getLdesMemberId());
        ldesFragmentRepository.saveFragment(ldesFragment);
    }

    private LdesFragment retrieveLastFragmentOrCreateNewFragment(LdesMember ldesMember) {
        return ldesFragmentRepository.retrieveOpenFragment(ldesConfig.getCollectionName())
                .map(fragment -> {
                    if (fragmentCreator.needsToCreateNewFragment(fragment)) {
                        return fragmentCreator.createNewFragment(Optional.of(fragment), ldesMember);
                    } else {
                        return fragment;
                    }
                })
                .orElseGet(() -> fragmentCreator.createNewFragment(Optional.empty(), ldesMember));
    }
}
