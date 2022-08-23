package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesmember.entities.LdesMember;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesmember.repository.LdesMemberRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.entities.LdesMemberEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.repositories.LdesMemberEntityRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

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
	public Optional<LdesMember> getLdesMemberById(String memberId) {
		return repository
				.findById(memberId)
				.map(LdesMemberEntity::toLdesMember);
	}

	@Override
	public Stream<LdesMember> getLdesMembersByIds(List<String> ids) {
		return StreamSupport.stream(repository.findAllById(ids).spliterator(), false)
				.map(LdesMemberEntity::toLdesMember);
	}

}
