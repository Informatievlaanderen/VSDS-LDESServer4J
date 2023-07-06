package be.vlaanderen.informatievlaanderen.ldes.server.ingest;

import be.vlaanderen.informatievlaanderen.ldes.server.ingest.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.entities.MemberEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.mapper.MemberEntityMapper;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.repositories.MemberRepository;
import org.springframework.stereotype.Component;

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
	public void deleteMembersByCollection(String collectionName) {
		memberEntityRepository.deleteAllByCollectionName(collectionName);
	}

	@Override
	public Stream<Member> getMemberStreamOfCollection(String collectionName) {
		return memberEntityRepository
				.getAllByCollectionNameOrderBySequenceNrAsc(collectionName)
				.map(memberEntityMapper::toMember);
	}

}
