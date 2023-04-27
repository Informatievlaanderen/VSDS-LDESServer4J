package be.vlaanderen.informatievlaanderen.ldes.server.ingest.repository;

import be.vlaanderen.informatievlaanderen.ldes.server.ingest.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.entities.MemberEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.mapper.MemberEntityMapper;
import org.springframework.stereotype.Component;

// TODO: 27/04/2023 komkommer testen
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

}
