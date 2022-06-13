package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.entities.LdesMember;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.repositories.LdesMemberRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.entities.LdesMemberEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.repositories.LdesMemberEntityRepository;

import java.util.List;
import java.util.stream.Collectors;

public class LdesMemberMongoRepository implements LdesMemberRepository {

    private final LdesMemberEntityRepository ldesMemberEntityRepository;

    public LdesMemberMongoRepository(final LdesMemberEntityRepository ldesMemberEntityRepository) {
        this.ldesMemberEntityRepository = ldesMemberEntityRepository;
    }

    @Override
    public LdesMember saveLdesMember(LdesMember ldesMember) {
        ldesMemberEntityRepository.save(LdesMemberEntity.fromLdesMember(ldesMember));
        return ldesMember;
    }

    @Override
    public List<LdesMember> fetchLdesMembers() {
        return ldesMemberEntityRepository.findAll().stream().map(LdesMemberEntity::toLdesMember)
                .collect(Collectors.toList());
    }
}
