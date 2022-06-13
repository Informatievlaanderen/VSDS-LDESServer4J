package be.vlaanderen.informatievlaanderen.ldes.server.domain.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.repositories.LdesMemberRepository;
import org.apache.jena.ext.com.google.common.collect.ImmutableMap;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.stream.Collectors;

@Component
public class FragmentCreatorImpl implements FragmentCreator {

    private final LdesMemberRepository ldesMemberRepository;

    public FragmentCreatorImpl(LdesMemberRepository ldesMemberRepository) {
        this.ldesMemberRepository = ldesMemberRepository;
    }

    @Override
    public LdesFragment createFragment(Map<String, String> ldesFragmentConfig) {
        return new LdesFragment(ldesMemberRepository.fetchLdesMembers().stream().limit(1).collect(Collectors.toList()),
                ldesFragmentConfig);
    }
}
