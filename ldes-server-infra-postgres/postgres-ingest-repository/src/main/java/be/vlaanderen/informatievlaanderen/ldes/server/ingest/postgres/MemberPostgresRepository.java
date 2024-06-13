package be.vlaanderen.informatievlaanderen.ldes.server.ingest.postgres;

import be.vlaanderen.informatievlaanderen.ldes.server.ingest.entities.IngestedMember;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.postgres.mapper.MemberEntityMapper;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.postgres.repository.MemberEntityRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.postgres.service.MemberEntityListener;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.repositories.MemberRepository;
import io.micrometer.core.instrument.Metrics;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.apache.jena.riot.Lang;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@Primary
public class MemberPostgresRepository implements MemberRepository {
	public static final Lang CONVERSION_LANG = Lang.RDFPROTO;
	private static final String LDES_SERVER_DELETED_MEMBERS_COUNT = "ldes_server_deleted_members_count";
	private final MemberEntityRepository repository;
	private final MemberEntityMapper mapper;
	private final EntityManager entityManager;

	public MemberPostgresRepository(MemberEntityRepository repository,
	                                MemberEntityMapper mapper, EntityManager entityManager) {
		this.repository = repository;
		this.mapper = mapper;
		this.entityManager = entityManager;
		MemberEntityListener.repository = repository;
	}

	public boolean memberExists(String memberId) {
		return repository.existsById(memberId);
	}

	@Override
	@Transactional
	public List<IngestedMember> insertAll(List<IngestedMember> members) {
		if (!membersContainDuplicateIds(members) && !membersExist(members)) {
			repository.saveAll(members.stream().map(mapper::toMemberEntity).toList());
			return members;
		}
		else {
			return List.of();
		}
	}

	protected boolean membersExist(List<IngestedMember> members) {
		return repository.existsByIdIn(members.stream().map(IngestedMember::getId).toList());
	}

	protected boolean membersContainDuplicateIds(List<IngestedMember> members) {
		return members.stream()
				       .map(IngestedMember::getId)
				       .collect(Collectors.toSet())
				       .size() != members.size();
	}

	@Override
	public Optional<IngestedMember> findById(String id) {
		return repository.findById(id).map(mapper::toMember);
	}

	@Override
	public Stream<IngestedMember> findAllByIds(List<String> memberIds) {
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
	public Stream<IngestedMember> getMemberStreamOfCollection(String collectionName) {
		return repository
				.getAllByCollectionNameOrderBySequenceNrAsc(collectionName)
				.stream()
				.map(mapper::toMember);
	}

	@Override
	@Transactional
	public void deleteMembers(List<String> memberIds) {
		repository.deleteAllById(memberIds);
		Metrics.counter(LDES_SERVER_DELETED_MEMBERS_COUNT).increment(memberIds.size());
	}

	@Override
	@Transactional
	public void removeFromEventSource(List<String> ids) {
		Query query = entityManager.createQuery("UPDATE MemberEntity m SET m.isInEventSource = false " +
		                                        "WHERE m.id IN :memberIds");
		query.setParameter("memberIds", ids);
		query.executeUpdate();
	}

	@Override
	public Optional<IngestedMember> findFirstByCollectionNameAndSequenceNrGreaterThanAndInEventSource(String collectionName, long sequenceNr) {
		return repository.findFirstByCollectionNameAndIsInEventSourceAndSequenceNrGreaterThanOrderBySequenceNrAsc(collectionName, true, sequenceNr)
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