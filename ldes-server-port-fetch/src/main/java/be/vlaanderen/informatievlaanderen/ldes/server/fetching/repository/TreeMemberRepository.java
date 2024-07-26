package be.vlaanderen.informatievlaanderen.ldes.server.fetching.repository;

import be.vlaanderen.informatievlaanderen.ldes.server.fetching.entities.Member;

import java.util.stream.Stream;

public interface TreeMemberRepository {
	Stream<Member> findAllByTreeNodeUrl(String url);
}
