package be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.repository;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public interface MemberRepository {

	Member saveLdesMember(Member member);

	boolean memberExists(String id);

	Optional<Member> getMember(String id);

	void deleteMember(String memberId);

	void deleteMembersByCollection(String collection);

	void addMemberReference(String memberId, String fragmentId);

	Stream<Member> getMembersByReference(String treeNodeId);

	void removeMemberReference(String memberId, String fragmentId);

	void removeViewReferences(ViewName viewName);

	Stream<Member> getMemberStreamOfCollection(String collectionName);

	List<Member> getMembersOfVersion(String versionOf);
}
