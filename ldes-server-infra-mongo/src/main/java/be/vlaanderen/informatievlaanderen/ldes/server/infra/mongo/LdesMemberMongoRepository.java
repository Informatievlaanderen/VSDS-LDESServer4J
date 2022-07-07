package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.entities.LdesMember;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.repositories.LdesMemberRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.entities.LdesMemberEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.repositories.LdesMemberEntityRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class LdesMemberMongoRepository implements LdesMemberRepository {

    private final LdesMemberEntityRepository repository;

    public LdesMemberMongoRepository(final LdesMemberEntityRepository repository) {
        this.repository = repository;
    }

    @Override
    public LdesMember saveLdesMember(LdesMember ldesMember) {
        repository.save(LdesMemberEntity.fromLdesMember(ldesMember));
        return ldesMember;
    }

    @Override
    public List<LdesMember> fetchLdesMembers() {
        return repository.findAll().stream().map(LdesMemberEntity::toLdesMember).toList();
    }

    @Override
    public LdesMember getLdesMemberById(String id) {
        return repository.findById(id).orElseThrow(()->new RuntimeException("")).toLdesMember();
    }

}
