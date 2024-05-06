package be.vlaanderen.informatievlaanderen.ldes.server.ingest.repositories;

import be.vlaanderen.informatievlaanderen.ldes.server.ingest.entities.Member;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public interface MemberRepository {

	boolean memberExists(String memberId);

	List<Member> insertAll(List<Member> members);

	Optional<Member> findById(String id);

	Stream<Member> findAllByIds(List<String> memberIds);

	void deleteMembersByCollection(String collectionName);

	Stream<Member> getMemberStreamOfCollection(String collectionName);

	void deleteMembers(List<String> memberId);

	void removeFromEventSource(List<String> ids);

	Optional<Member> findFirstByCollectionNameAndSequenceNrGreaterThanAndInEventSource(String collectionName, long sequenceNr);

	long getMemberCount();

	long getMemberCountOfCollection(String collectionName);

}
