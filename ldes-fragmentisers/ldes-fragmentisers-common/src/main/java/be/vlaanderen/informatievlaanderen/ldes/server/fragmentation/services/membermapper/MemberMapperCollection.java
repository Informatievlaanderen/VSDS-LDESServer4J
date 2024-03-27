package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.services.membermapper;


import java.util.Optional;

public interface MemberMapperCollection {
    Optional<MemberMapper> getMemberMapper(String collectionName);
    void addMemberMapper(String collectionName, MemberMapper memberMapper);
    void deleteMemberMapper(String collectionName);
}
