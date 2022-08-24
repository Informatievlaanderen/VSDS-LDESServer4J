package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.entities.FragmentPair;

import java.util.List;

public abstract class FragmentationServiceDecorator implements FragmentationService{

    final FragmentationService fragmentationService;

    protected FragmentationServiceDecorator(FragmentationService fragmentationService) {
        this.fragmentationService = fragmentationService;
    }

    @Override
    public void addMemberToFragment(List<FragmentPair> fragmentPairList, String ldesMemberId) {
        fragmentationService.addMemberToFragment(fragmentPairList, ldesMemberId);
    }
}
