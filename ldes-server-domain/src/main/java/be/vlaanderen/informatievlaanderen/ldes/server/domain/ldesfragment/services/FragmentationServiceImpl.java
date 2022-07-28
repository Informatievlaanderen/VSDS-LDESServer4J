package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.config.LdesConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.exceptions.MemberNotFoundException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRespository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesmember.entities.LdesMember;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesmember.repository.LdesMemberRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class FragmentationServiceImpl implements FragmentationService{

    private final LdesConfig ldesConfig;
    private final LdesFragmentRespository ldesFragmentRespository;
    private final FragmentCreator fragmentCreator;
    private final LdesMemberRepository ldesMemberRepository;

    public FragmentationServiceImpl(LdesConfig ldesConfig, LdesFragmentRespository ldesFragmentRespository, FragmentCreator fragmentCreator, LdesMemberRepository ldesMemberRepository) {
        this.ldesConfig = ldesConfig;
        this.ldesFragmentRespository = ldesFragmentRespository;
        this.fragmentCreator = fragmentCreator;
        this.ldesMemberRepository = ldesMemberRepository;
    }

    public void addMemberToFragment(String ldesMemberId){
        LdesMember ldesMember=ldesMemberRepository.getLdesMemberById(ldesMemberId).orElseThrow(()->new MemberNotFoundException(ldesMemberId));
        LdesFragment ldesFragment = retrieveLastFragmentOrCreateNewFragment(ldesMember);
        ldesFragment.addMember(ldesMember.getLdesMemberId());
        ldesFragmentRespository.saveFragment(ldesFragment);
    }


    private LdesFragment retrieveLastFragmentOrCreateNewFragment(LdesMember ldesMember) {
        return ldesFragmentRespository.retrieveOpenFragment(ldesConfig.getCollectionName())
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
