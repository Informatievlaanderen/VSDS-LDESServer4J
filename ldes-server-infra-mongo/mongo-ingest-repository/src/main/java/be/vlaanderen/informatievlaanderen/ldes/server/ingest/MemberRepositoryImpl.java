package be.vlaanderen.informatievlaanderen.ldes.server.ingest;

import be.vlaanderen.informatievlaanderen.ldes.server.ingest.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.entities.MemberEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.mapper.MemberEntityMapper;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.membersequence.IngestMemberSequenceService;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.repositories.MemberRepository;
import io.micrometer.core.instrument.Metrics;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Component
public class MemberRepositoryImpl implements MemberRepository {
	public static final String ID = "_id";
	private static final String IN_EVENTSOURCE = "isInEventSource";
	private static final String LDES_SERVER_DELETED_MEMBERS_COUNT = "ldes_server_deleted_members_count";
	private final MongoTemplate mongoTemplate;
	private final MemberEntityRepository memberEntityRepository;
	private final MemberEntityMapper memberEntityMapper;
	private final IngestMemberSequenceService sequenceService;

	public MemberRepositoryImpl(MemberEntityRepository memberEntityRepository,
								MemberEntityMapper memberEntityMapper,
								IngestMemberSequenceService sequenceService,
								MongoTemplate mongoTemplate) {
		this.memberEntityRepository = memberEntityRepository;
		this.memberEntityMapper = memberEntityMapper;
		this.sequenceService = sequenceService;
		this.mongoTemplate = mongoTemplate;
	}

	public boolean memberExists(String memberId) {
		return memberEntityRepository.existsById(memberId);
	}

	@Override
	public List<Member> insertAll(List<Member> members) {
		try {
			List<MemberEntity> memberEntities = members.stream().map(memberEntityMapper::toMemberEntity).toList();
			return memberEntityRepository.insert(memberEntities).stream().map(memberEntityMapper::toMember).toList();
		} catch (DuplicateKeyException e) {
			return List.of();
		}
	}

	@Override
	public Optional<Member> findById(String id) {
		return memberEntityRepository.findById(id).map(memberEntityMapper::toMember);
	}

	@Override
	public Stream<Member> findAllByIds(List<String> memberIds) {
		return memberEntityRepository.findAllByIdIn(memberIds)
				.map(memberEntityMapper::toMember);
	}

	@Override
	public void deleteMembersByCollection(String collectionName) {
		memberEntityRepository.deleteAllByCollectionName(collectionName);
		sequenceService.removeSequence(collectionName);
	}

	@Override
	public Stream<Member> getMemberStreamOfCollection(String collectionName) {
		return memberEntityRepository
				.getAllByCollectionNameOrderBySequenceNrAsc(collectionName)
				.map(memberEntityMapper::toMember);
	}

	@Override
	public void deleteMembers(List<String> memberIds) {
		memberEntityRepository.deleteAllById(memberIds);
		Metrics.counter(LDES_SERVER_DELETED_MEMBERS_COUNT).increment(memberIds.size());
	}

	@Override
	public void removeFromEventSource(List<String> ids) {
		Query query = new Query();
		query.addCriteria(Criteria.where(ID).in(ids));
		Update update = new Update();
		update.set(IN_EVENTSOURCE, false);

		mongoTemplate.updateMulti(query, update, MemberEntity.class);
	}

	@Override
	public Optional<Member> findFirstByCollectionNameAndSequenceNrGreaterThanAndInEventSource(String collectionName, long sequenceNr) {
		return memberEntityRepository
				.findFirstByCollectionNameAndInEventSourceAndSequenceNrGreaterThanOrderBySequenceNrAsc(collectionName, true, sequenceNr)
				.map(memberEntityMapper::toMember);
	}

	@Override
	public long getMemberCount() {
		return memberEntityRepository.count();
	}

	@Override
	public long getMemberCountOfCollection(String collectionName) {
		return memberEntityRepository.countByCollectionName(collectionName);
	}

}