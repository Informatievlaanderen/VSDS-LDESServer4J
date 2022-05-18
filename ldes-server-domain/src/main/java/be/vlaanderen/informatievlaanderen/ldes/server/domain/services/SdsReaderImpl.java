package be.vlaanderen.informatievlaanderen.ldes.server.domain.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.entities.LdesMember;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.repositories.LdesMemberRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SdsReaderImpl implements SdsReader {

    private final LdesMemberRepository ldesMemberRepository;

    @Autowired
    public SdsReaderImpl(final LdesMemberRepository ldesMemberRepository) {
        this.ldesMemberRepository = ldesMemberRepository;
    }

    @Override
    public LdesMember storeLdesMember(LdesMember ldesMember) {
        return ldesMemberRepository.saveLdesMember(ldesMember);
    }
}
