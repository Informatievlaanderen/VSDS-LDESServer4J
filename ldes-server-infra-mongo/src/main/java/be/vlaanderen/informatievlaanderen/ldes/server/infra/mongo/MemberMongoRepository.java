package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.repository.MemberRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.entities.LdesMemberEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.repositories.LdesMemberEntityRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Component
public class MemberMongoRepository implements MemberRepository {

	private final LdesMemberEntityRepository repository;

	public MemberMongoRepository(final LdesMemberEntityRepository repository) {
		this.repository = repository;
	}

	@Override
	@Transactional
	public Member saveLdesMember(Member member) {
		repository.save(LdesMemberEntity.fromLdesMember(member));
		return member;
	}

	@Override
	@Transactional(readOnly = true)
	public boolean memberExists(String memberId) {
		return repository.existsById(memberId);
	}

	@Override
	@Transactional(readOnly = true)
	public Stream<Member> getLdesMembersByIds(List<String> ids) {
		return StreamSupport.stream(repository.findAllById(ids).spliterator(), false)
				.map(LdesMemberEntity::toLdesMember);
	}

	@Override
	@Transactional
	public void deleteMember(String memberId) {
		repository.deleteById(memberId);
	}
}
