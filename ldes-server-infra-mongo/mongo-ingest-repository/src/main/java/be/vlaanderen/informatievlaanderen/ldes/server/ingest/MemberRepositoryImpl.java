package be.vlaanderen.informatievlaanderen.ldes.server.ingest;

import be.vlaanderen.informatievlaanderen.ldes.server.ingest.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.entities.MemberEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.mapper.MemberEntityMapper;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.membersequence.IngestMemberSequenceService;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.repositories.MemberRepository;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.Metrics;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static be.vlaanderen.informatievlaanderen.ldes.server.ingest.MemberIngesterImpl.LDES_SERVER_ACTUAL_MEMBERS_COUNT;

@Component
public class MemberRepositoryImpl implements MemberRepository {

	private final MemberEntityRepository memberEntityRepository;
	private final MemberEntityMapper memberEntityMapper;
	private final IngestMemberSequenceService sequenceService;
	private final MongoTemplate mongoTemplate;

	public MemberRepositoryImpl(MemberEntityRepository memberEntityRepository,
			MemberEntityMapper memberEntityMapper,
			IngestMemberSequenceService sequenceService, MongoTemplate mongoTemplate) {
		this.memberEntityRepository = memberEntityRepository;
		this.memberEntityMapper = memberEntityMapper;
		this.sequenceService = sequenceService;
		this.mongoTemplate = mongoTemplate;
		Gauge.builder(LDES_SERVER_ACTUAL_MEMBERS_COUNT, this::estimatedCountMembers).register(Metrics.globalRegistry);
	}

	public boolean memberExists(String memberId) {
		return memberEntityRepository.existsById(memberId);
	}

	public Optional<Member> insert(Member member) {
		MemberEntity memberEntityToSave = memberEntityMapper.toMemberEntity(member);
		try {
			MemberEntity savedMember = memberEntityRepository.insert(memberEntityToSave);
			return Optional.of(memberEntityMapper.toMember(savedMember));
		} catch (DuplicateKeyException e) {
			return Optional.empty();
		}
	}

	@Override
	public Optional<Member> findById(String id) {
		return memberEntityRepository.findById(id).map(memberEntityMapper::toMember);
	}

	@Override
	public List<Member> findAllByIds(List<String> memberIds) {
		return memberEntityRepository.findAllByIdIn(memberIds)
				.map(memberEntityMapper::toMember)
				.toList();
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
	public void deleteMember(String memberId) {
		memberEntityRepository.deleteById(memberId);
	}

	@Override
	public Optional<Member> findFirstByCollectionNameAndSequenceNrGreaterThan(String collectionName, long sequenceNr) {
		return memberEntityRepository
				.findFirstByCollectionNameAndSequenceNrGreaterThanOrderBySequenceNrAsc(collectionName, sequenceNr)
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

	@Override
	public long getTotalSequence() {
		return sequenceService.getTotalSequence();
	}

	@Override
	public long getSequenceForCollection(String collectionName) {
		return sequenceService.getSequenceForCollection(collectionName);
	}
	private long estimatedCountMembers() {
		return mongoTemplate.estimatedCount("ingest_ldesmember");
	}

}