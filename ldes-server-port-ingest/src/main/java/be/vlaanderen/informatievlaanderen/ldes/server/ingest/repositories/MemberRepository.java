package be.vlaanderen.informatievlaanderen.ldes.server.ingest.repositories;

import be.vlaanderen.informatievlaanderen.ldes.server.ingest.entities.Member;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public interface MemberRepository {

	boolean memberExists(String memberId);

	Member saveMember(Member member);

	Optional<Member> findById(String id);

	List<Member> findAllByIds(List<String> memberIds);

	void deleteMembersByCollection(String collectionName);

	Stream<Member> getMemberStreamOfCollection(String collectionName);

	void deleteMember(String memberId);

	Optional<Member> findFirstByCollectionNameAndSequenceNrGreaterThan(String collectionName, long sequenceNr);
}
