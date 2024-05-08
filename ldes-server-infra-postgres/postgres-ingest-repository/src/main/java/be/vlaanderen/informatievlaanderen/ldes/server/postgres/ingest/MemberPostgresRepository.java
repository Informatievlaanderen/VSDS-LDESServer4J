package be.vlaanderen.informatievlaanderen.ldes.server.postgres.ingest;

import be.vlaanderen.informatievlaanderen.ldes.server.ingest.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.repositories.MemberRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.postgres.ingest.mapper.MemberEntityMapper;
import be.vlaanderen.informatievlaanderen.ldes.server.postgres.ingest.repository.MemberEntityRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.postgres.ingest.service.MemberEntityListener;
import io.micrometer.core.instrument.Metrics;
import org.apache.jena.riot.Lang;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class MemberPostgresRepository implements MemberRepository {
	public static final Lang CONVERSION_LANG = Lang.RDFPROTO;
	private static final String LDES_SERVER_DELETED_MEMBERS_COUNT = "ldes_server_deleted_members_count";
	private final MemberEntityRepository repository;
	private final MemberEntityMapper mapper;

	public MemberPostgresRepository(MemberEntityRepository repository,
	                                MemberEntityMapper mapper) {
		this.repository = repository;
		this.mapper = mapper;
		MemberEntityListener.repository = repository;
	}

	public boolean memberExists(String memberId) {
		return repository.existsById(memberId);
	}

	@Override
	@Transactional
	public List<Member> insertAll(List<Member> members) {
		if (!membersContainDuplicateIds(members) && !membersExist(members)) {
			repository.saveAll(members.stream().map(mapper::toMemberEntity).toList());
			return members;
		}
		else {
			return List.of();
		}
	}

	protected boolean membersExist(List<Member> members) {
		return repository.existsByIdIn(members.stream().map(Member::getId).toList());
	}

	protected boolean membersContainDuplicateIds(List<Member> members) {
		return members.stream()
				       .map(Member::getId)
				       .collect(Collectors.toSet())
				       .size() != members.size();
	}

	@Override
	public Optional<Member> findById(String id) {
		return repository.findById(id).map(mapper::toMember);
	}

	@Override
	public Stream<Member> findAllByIds(List<String> memberIds) {
		return repository.findAllByIdIn(memberIds)
				.stream()
				.map(mapper::toMember);
	}

	@Override
	@Transactional
	public void deleteMembersByCollection(String collectionName) {
		repository.deleteAllByCollectionName(collectionName);
	}

	@Override
	public Stream<Member> getMemberStreamOfCollection(String collectionName) {
		return repository
				.getAllByCollectionNameOrderBySequenceNrAsc(collectionName)
				.stream()
				.map(mapper::toMember);
	}

	@Override
	@Transactional
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