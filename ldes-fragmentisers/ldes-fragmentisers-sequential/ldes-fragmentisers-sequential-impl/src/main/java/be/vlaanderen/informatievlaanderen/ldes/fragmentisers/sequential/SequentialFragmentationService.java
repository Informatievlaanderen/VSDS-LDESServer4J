package be.vlaanderen.informatievlaanderen.ldes.fragmentisers.sequential;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.config.LdesConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.MemberNotFoundException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentCreator;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentationService;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesmember.entities.LdesMember;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesmember.repository.LdesMemberRepository;

import java.util.Optional;

public class SequentialFragmentationService implements FragmentationService {

    protected final LdesConfig ldesConfig;
    protected final FragmentCreator fragmentCreator;
    protected final LdesMemberRepository ldesMemberRepository;
    protected final LdesFragmentRepository ldesFragmentRepository;

    public SequentialFragmentationService(LdesConfig ldesConfig, FragmentCreator fragmentCreator, LdesMemberRepository ldesMemberRepository, LdesFragmentRepository ldesFragmentRepository) {
        this.ldesConfig = ldesConfig;
        this.fragmentCreator = fragmentCreator;
        this.ldesMemberRepository = ldesMemberRepository;
        this.ldesFragmentRepository = ldesFragmentRepository;
    }

    public void addMemberToFragment(String ldesMemberId) {
        LdesMember ldesMember = ldesMemberRepository.getLdesMemberById(ldesMemberId).orElseThrow(() -> new MemberNotFoundException(ldesMemberId));
        LdesFragment ldesFragment = retrieveLastFragmentOrCreateNewFragment();
        ldesFragment.addMember(ldesMember.getLdesMemberId());
        ldesFragmentRepository.saveFragment(ldesFragment);
    }


    private LdesFragment retrieveLastFragmentOrCreateNewFragment() {
        return ldesFragmentRepository.retrieveOpenFragment(ldesConfig.getCollectionName())
                .map(fragment -> {
                    if (fragmentCreator.needsToCreateNewFragment(fragment)) {
                        return fragmentCreator.createNewFragment(Optional.of(fragment), null);
                    } else {
                        return fragment;
                    }
                })
                .orElseGet(() -> fragmentCreator.createNewFragment(Optional.empty(), null));
    }
}
