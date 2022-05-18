package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.entities.LdesMember;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.repositories.LdesMemberRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.converters.LdesMemberConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.entities.LdesMemberEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.repositories.LdesMemberEntityRepository;

import java.util.List;
import java.util.stream.Collectors;

public class LdesMemberMongoRepository implements LdesMemberRepository {

    private final LdesMemberEntityRepository ldesMemberEntityRepository;
    private final LdesMemberConverter ldesMemberConverter;

    public LdesMemberMongoRepository(final LdesMemberEntityRepository ldesMemberEntityRepository,
            final LdesMemberConverter ldesMemberConverter) {
        this.ldesMemberEntityRepository = ldesMemberEntityRepository;
        this.ldesMemberConverter = ldesMemberConverter;
    }

    @Override
    public LdesMember saveLdesMember(LdesMember ldesMember) {
        LdesMemberEntity ldesMemberEntity = ldesMemberConverter.toEntity(ldesMember);
        LdesMemberEntity savedLdesMemberEntity = ldesMemberEntityRepository.save(ldesMemberEntity);
        return ldesMemberConverter.fromEntity(savedLdesMemberEntity);
    }

    @Override
    public List<LdesMember> fetchLdesMembers() {
        return ldesMemberEntityRepository.findAll().stream().map(ldesMemberConverter::fromEntity)
                .collect(Collectors.toList());
    }
}