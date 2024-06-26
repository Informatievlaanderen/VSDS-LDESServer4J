package be.vlaanderen.informatievlaanderen.ldes.server.ingest.repositories;

import be.vlaanderen.informatievlaanderen.ldes.server.ingest.entities.IngestedMember;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public interface MemberRepository {

	boolean memberExists(String memberId);

	List<IngestedMember> insertAll(List<IngestedMember> members);

	Optional<IngestedMember> findById(String id);

	Stream<IngestedMember> findAllByIds(List<String> memberIds);

	void deleteMembersByCollection(String collectionName);

	Stream<IngestedMember> getMemberStreamOfCollection(String collectionName);

	void deleteMembers(List<String> memberId);

	void removeFromEventSource(List<String> ids);

	Optional<IngestedMember> findFirstByCollectionNameAndSequenceNrGreaterThanAndInEventSource(String collectionName, long sequenceNr);

	long getMemberCount();

	long getMemberCountOfCollection(String collectionName);

}
