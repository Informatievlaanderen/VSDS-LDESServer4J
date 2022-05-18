package be.vlaanderen.informatievlaanderen.ldes.server.domain.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.repositories.LdesMemberRepository;
import org.springframework.stereotype.Component;

@Component
public class FragmentCreatorImpl implements FragmentCreator {

    private final LdesMemberRepository ldesMemberRepository;

    public FragmentCreatorImpl(LdesMemberRepository ldesMemberRepository) {
        this.ldesMemberRepository = ldesMemberRepository;
    }

    @Override
    public LdesFragment createFragment() {
        return new LdesFragment(ldesMemberRepository.fetchLdesMembers());
    }
}
