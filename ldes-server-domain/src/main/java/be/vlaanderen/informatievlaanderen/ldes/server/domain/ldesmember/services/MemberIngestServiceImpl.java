package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesmember.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.config.LdesConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRespository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentCreator;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesmember.entities.LdesMember;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesmember.repository.LdesMemberRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class MemberIngestServiceImpl implements MemberIngestService {

    private final LdesConfig ldesConfig;
    private final LdesMemberRepository ldesMemberRepository;
    private final LdesFragmentRespository ldesFragmentRespository;

    private final FragmentCreator fragmentCreator;

    public MemberIngestServiceImpl(LdesConfig ldesConfig, LdesMemberRepository ldesMemberRepository, LdesFragmentRespository ldesFragmentRespository, FragmentCreator fragmentCreator) {
        this.ldesConfig = ldesConfig;
        this.ldesMemberRepository = ldesMemberRepository;
        this.ldesFragmentRespository = ldesFragmentRespository;
        this.fragmentCreator = fragmentCreator;
    }

    @Override
    public LdesMember addMember(LdesMember ldesMember) {
        Optional<LdesMember> optionalLdesMember = ldesMemberRepository.getLdesMemberById(ldesMember.getLdesMemberId());
        return optionalLdesMember.orElseGet(() -> addMemberToFragmentAndStore(ldesMember));
    }

    private LdesMember addMemberToFragmentAndStore(LdesMember ldesMember) {
        ldesMember.replaceTreeMemberStatement(ldesConfig.getHostName(), ldesConfig.getCollectionName());
        LdesFragment ldesFragment = retrieveLastFragmentOrCreateNewFragment(ldesMember);
        ldesFragment.addMember(ldesMember.getLdesMemberId());
        ldesFragmentRespository.saveFragment(ldesFragment);
        return ldesMemberRepository.saveLdesMember(ldesMember);
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
