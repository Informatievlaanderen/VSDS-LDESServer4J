package be.vlaanderen.informatievlaanderen.ldes.server.ingest.repositories;

import be.vlaanderen.informatievlaanderen.ldes.server.ingest.entities.IngestedMember;

import java.util.List;
import java.util.stream.Stream;

public interface MemberRepository {
	int insertAll(List<IngestedMember> members);

	Stream<IngestedMember> findAllByCollectionAndSubject(String collectionName, List<String> subjects);

	void deleteMembersByCollectionNameAndSubjects(String collectionName, List<String> subjects);
}
