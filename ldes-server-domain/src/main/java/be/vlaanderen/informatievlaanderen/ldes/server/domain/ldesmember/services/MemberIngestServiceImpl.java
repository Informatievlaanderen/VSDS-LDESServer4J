package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesmember.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentationQueueMediator;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesmember.entities.LdesMember;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesmember.repository.LdesMemberRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class MemberIngestServiceImpl implements MemberIngestService {

    private final LdesMemberRepository ldesMemberRepository;

    private final FragmentationQueueMediator fragmentationQueueMediator;

    public MemberIngestServiceImpl(LdesMemberRepository ldesMemberRepository, FragmentationQueueMediator fragmentationQueueMediator) {
        this.ldesMemberRepository = ldesMemberRepository;
        this.fragmentationQueueMediator = fragmentationQueueMediator;
    }

    @Override
    public void addMember(LdesMember ldesMember) {
        Optional<LdesMember> optionalLdesMember = ldesMemberRepository.getLdesMemberById(ldesMember.getLdesMemberId());
        LdesMember savedLdesMember = optionalLdesMember.orElseGet(() -> storeLdesMember(ldesMember));
        fragmentationQueueMediator.addLdesMember(savedLdesMember.getLdesMemberId());
    }

    private LdesMember storeLdesMember(LdesMember ldesMember) {
        ldesMember.removeTreeMember();
        return ldesMemberRepository.saveLdesMember(ldesMember);
    }


}
