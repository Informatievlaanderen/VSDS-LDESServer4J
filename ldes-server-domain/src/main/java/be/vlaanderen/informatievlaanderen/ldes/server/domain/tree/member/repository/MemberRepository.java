package be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.repository;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.entities.Member;

import java.util.List;
import java.util.stream.Stream;

public interface MemberRepository {

	Member saveLdesMember(Member member);

	boolean memberExists(String id);

	Stream<Member> getLdesMembersByIds(List<String> ids);

	void deleteMember(String memberId);

	void addMemberReference(String ldesMemberId, String fragmentId);

	List<Member> getMembersByReference(String treeNodeId);
}
