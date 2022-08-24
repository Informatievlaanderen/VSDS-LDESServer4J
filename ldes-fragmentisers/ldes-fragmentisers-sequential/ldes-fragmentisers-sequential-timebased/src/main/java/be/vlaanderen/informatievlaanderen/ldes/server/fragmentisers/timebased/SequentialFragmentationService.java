package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebased;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.config.LdesConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.MemberNotFoundException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentCreator;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentationService;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentationServiceDecorator;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.entities.FragmentPair;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesmember.entities.LdesMember;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesmember.repository.LdesMemberRepository;

import java.util.List;
import java.util.Optional;

public class SequentialFragmentationService extends FragmentationServiceDecorator {

    protected final LdesConfig ldesConfig;
    protected final FragmentCreator fragmentCreator;
    protected final LdesMemberRepository ldesMemberRepository;
    protected final LdesFragmentRepository ldesFragmentRepository;

    public SequentialFragmentationService(FragmentationService fragmentationService, LdesConfig ldesConfig, FragmentCreator fragmentCreator,
                                          LdesMemberRepository ldesMemberRepository, LdesFragmentRepository ldesFragmentRepository) {
        super(fragmentationService);
        this.ldesConfig = ldesConfig;
        this.fragmentCreator = fragmentCreator;
        this.ldesMemberRepository = ldesMemberRepository;
        this.ldesFragmentRepository = ldesFragmentRepository;
    }

    public void addMemberToFragment(List<FragmentPair> fragmentPairList, String ldesMemberId) {
        LdesFragment ldesFragment = retrieveLastFragmentOrCreateNewFragment(fragmentPairList);
        if (!ldesFragment.getMemberIds().contains(ldesMemberId)) {
            System.out.println("Added member to " + ldesFragment.getFragmentId());
            ldesFragmentRepository.saveFragment(ldesFragment);
            super.addMemberToFragment(ldesFragment.getFragmentInfo().getFragmentPairs(), ldesMemberId);
        }
    }

    private LdesFragment retrieveLastFragmentOrCreateNewFragment(List<FragmentPair> fragmentPairList) {
        return ldesFragmentRepository.retrieveOpenFragment(ldesConfig.getCollectionName(), fragmentPairList)
                .map(fragment -> {
                    if (fragmentCreator.needsToCreateNewFragment(fragment)) {
                        return fragmentCreator.createNewFragment(Optional.of(fragment), fragmentPairList);
                    } else {
                        return fragment;
                    }
                })
                .orElseGet(() -> fragmentCreator.createNewFragment(Optional.empty(), fragmentPairList));
    }
}
