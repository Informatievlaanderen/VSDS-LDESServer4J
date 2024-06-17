package be.vlaanderen.informatievlaanderen.ldes.server.ingest.repositories;

import be.vlaanderen.informatievlaanderen.ldes.server.ingest.entities.IngestedMember;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public interface MemberRepository {

	boolean memberExists(String memberId);

	List<IngestedMember> insertAll(List<IngestedMember> members);

	Stream<IngestedMember> findAllByIds(List<String> memberIds);

	void deleteMembersByCollection(String collectionName);

	Stream<IngestedMember> getMemberStreamOfCollection(String collectionName);

	void deleteMembers(List<String> memberId);

	void removeFromEventSource(List<String> ids);
}
