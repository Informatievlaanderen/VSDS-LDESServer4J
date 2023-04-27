package be.vlaanderen.informatievlaanderen.ldes.server.ingest.repository;

import be.vlaanderen.informatievlaanderen.ldes.server.ingest.entities.Member;

public interface MemberRepository {

    boolean memberExists(String memberId);

    Member saveMember(Member member);

}
