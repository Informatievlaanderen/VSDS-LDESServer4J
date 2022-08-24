package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.config.LdesConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.MemberNotFoundException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.entities.FragmentPair;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.entities.LdesFragmentRequest;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesmember.entities.LdesMember;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesmember.repository.LdesMemberRepository;

import java.util.List;

public class FragmentationServiceImpl implements FragmentationService{
    private final LdesFragmentRepository ldesFragmentRepository;
    private final LdesMemberRepository ldesMemberRepository;
    private final LdesConfig ldesConfig;

    public FragmentationServiceImpl(LdesFragmentRepository ldesFragmentRepository, LdesMemberRepository ldesMemberRepository, LdesConfig ldesConfig) {
        this.ldesFragmentRepository = ldesFragmentRepository;
        this.ldesMemberRepository = ldesMemberRepository;
        this.ldesConfig = ldesConfig;
    }

    @Override
    public void addMemberToFragment(List<FragmentPair> fragmentPairList, String ldesMemberId) {
        LdesMember ldesMember = ldesMemberRepository.getLdesMemberById(ldesMemberId)
                .orElseThrow(() -> new MemberNotFoundException(ldesMemberId));
        LdesFragment ldesFragment = ldesFragmentRepository.retrieveFragment( new LdesFragmentRequest(ldesConfig.getCollectionName(),fragmentPairList)).orElseThrow(()->new RuntimeException(""));
        ldesFragment.addMember(ldesMember.getLdesMemberId());
        ldesFragmentRepository.saveFragment(ldesFragment);
    }


}
