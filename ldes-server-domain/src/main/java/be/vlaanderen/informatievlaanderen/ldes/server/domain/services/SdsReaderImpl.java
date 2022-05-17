package be.vlaanderen.informatievlaanderen.ldes.server.domain.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.entities.LdesMember;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.repositories.LdesMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class SdsReaderImpl implements SdsReader {

    private final LdesMemberRepository ldesMemberRepository;

    @Override
    public LdesMember storeLdesMember(LdesMember ldesMember) {
        return ldesMemberRepository.saveLdesMember(ldesMember);
    }
}
