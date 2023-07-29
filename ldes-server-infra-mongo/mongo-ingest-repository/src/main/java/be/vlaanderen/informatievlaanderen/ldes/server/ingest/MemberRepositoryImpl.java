package be.vlaanderen.informatievlaanderen.ldes.server.ingest;

import be.vlaanderen.informatievlaanderen.ldes.server.ingest.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.entities.MemberEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.mapper.MemberEntityMapper;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.repositories.MemberRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Component
public class MemberRepositoryImpl implements MemberRepository {

	private final MemberEntityRepository memberEntityRepository;
	private final MemberEntityMapper memberEntityMapper;

	public MemberRepositoryImpl(MemberEntityRepository memberEntityRepository,
			MemberEntityMapper memberEntityMapper) {
		this.memberEntityRepository = memberEntityRepository;
		this.memberEntityMapper = memberEntityMapper;
	}

	public boolean memberExists(String memberId) {
		return memberEntityRepository.existsById(memberId);
	}

	public Member saveMember(Member member) {
		MemberEntity memberEntityToSave = memberEntityMapper.toMemberEntity(member);
		MemberEntity savedMemberEntity = memberEntityRepository.save(memberEntityToSave);
		return memberEntityMapper.toMember(savedMemberEntity);
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

	// TODO TVB: 29/07/23 test with passing seq 3 and first one is seq 5
	@Override
	public Optional<Member> findFirstByCollectionNameAndSequenceNrGreaterThan(String collectionName, long sequenceNr) {
		return memberEntityRepository
				.findFirstByCollectionNameAndSequenceNrGreaterThanOrderBySequenceNrAsc(collectionName, sequenceNr)
				.map(memberEntityMapper::toMember);
	}
}
