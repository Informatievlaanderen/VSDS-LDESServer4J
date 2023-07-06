package be.vlaanderen.informatievlaanderen.ldes.server.ingest.repositories;

import be.vlaanderen.informatievlaanderen.ldes.server.ingest.entities.Member;

import java.util.Optional;

public interface MemberRepository {

	boolean memberExists(String memberId);

	Member saveMember(Member member);

	Optional<Member> findById(String id);

	void deleteMembersByCollection(String collectionName);
}
