package be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.repository;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.entities.Member;

import java.util.List;
import java.util.stream.Stream;

public interface MemberRepository {

	boolean saveLdesMember(Member member);

	Stream<Member> getLdesMembersByIds(List<String> ids);

	boolean deleteMember(String memberId);

	boolean addMemberReference(String ldesMemberId, String fragmentId);

	boolean removeMemberReference(String memberId, String fragmentId);
}
