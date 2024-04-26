package be.vlaanderen.informatievlaanderen.ldes.server.ingest;

import be.vlaanderen.informatievlaanderen.ldes.server.ingest.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.entity.MemberEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.mapper.MemberEntityMapper;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.repositories.MemberRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.repository.MemberEntityRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.service.MemberEntityListener;
import io.micrometer.core.instrument.Metrics;
import jakarta.persistence.EntityManager;
import org.apache.jena.riot.Lang;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Component
public class MemberPostgresRepository implements MemberRepository {
	public static final Lang CONVERSION_LANG = Lang.TTL;
	private static final String LDES_SERVER_DELETED_MEMBERS_COUNT = "ldes_server_deleted_members_count";
	private final MemberEntityRepository repository;
	private final MemberEntityMapper mapper;

	public MemberPostgresRepository(MemberEntityRepository repository,
	                                MemberEntityMapper mapper, EntityManager entityManager) {
		this.repository = repository;
		this.mapper = mapper;
		MemberEntityListener.entityManager = entityManager;
	}

	public boolean memberExists(String memberId) {
		return repository.existsById(memberId);
	}

	@Override
	public List<Member> insertAll(List<Member> members) {
		try {
			List<MemberEntity> memberEntities = members.stream().map(mapper::toMemberEntity).toList();
			return repository.saveAll(memberEntities).stream().map(mapper::toMember).toList();
		} catch (DuplicateKeyException e) {
			return List.of();
		}
	}

	@Override
	public Optional<Member> findById(String id) {
		return repository.findById(id).map(mapper::toMember);
	}

	@Override
	public Stream<Member> findAllByIds(List<String> memberIds) {
		return repository.findAllByIdIn(memberIds)
				.map(mapper::toMember);
	}

	@Override
	public void deleteMembersByCollection(String collectionName) {
		repository.deleteAllByCollectionName(collectionName);
	}

	@Override
	public Stream<Member> getMemberStreamOfCollection(String collectionName) {
		return repository
				.getAllByCollectionNameOrderBySequenceNrAsc(collectionName)
				.map(mapper::toMember);
	}

	@Override
	public void deleteMember(String memberId) {
		repository.deleteById(memberId);
		Metrics.counter(LDES_SERVER_DELETED_MEMBERS_COUNT).increment();
	}

	@Override
	public Optional<Member> findFirstByCollectionNameAndSequenceNrGreaterThan(String collectionName, long sequenceNr) {
		return repository
				.findFirstByCollectionNameAndSequenceNrGreaterThanOrderBySequenceNrAsc(collectionName, sequenceNr)
				.map(mapper::toMember);
	}

	@Override
	public long getMemberCount() {
		return repository.count();
	}

	@Override
	public long getMemberCountOfCollection(String collectionName) {
		return repository.countByCollectionName(collectionName);
	}

}