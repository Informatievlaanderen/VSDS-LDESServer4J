package be.vlaanderen.informatievlaanderen.ldes.server.ingest.repositories;

import be.vlaanderen.informatievlaanderen.ldes.server.ingest.entities.IngestedMember;

import java.util.List;
import java.util.stream.Stream;

public interface MemberRepository {

	List<IngestedMember> insertAll(List<IngestedMember> members);

	Stream<IngestedMember> findAllByIds(List<String> memberIds);

	List<IngestedMember> getMembersOfCollection(String collectionName);

	Stream<IngestedMember> findAllByCollectionAndSubject(String collectionName, List<String> subject);

	void deleteMembers(List<String> memberId);

	void deleteMembersByCollectionNameAndSubjects(String collectionName, List<String> oldIds);

	void removeFromEventSource(List<String> ids);
}
