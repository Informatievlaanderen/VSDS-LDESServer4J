package be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.repository;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.entities.Member;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public interface MemberRepository {

	Member saveLdesMember(Member member);

	Optional<Member> getLdesMemberById(String id);

	Stream<Member> getLdesMembersByIds(List<String> ids);
}
