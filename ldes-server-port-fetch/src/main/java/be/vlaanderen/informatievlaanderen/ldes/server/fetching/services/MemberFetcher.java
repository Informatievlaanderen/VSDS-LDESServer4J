package be.vlaanderen.informatievlaanderen.ldes.server.fetching.services;

import be.vlaanderen.informatievlaanderen.ldes.server.fetching.entities.Member;

import java.util.List;
import java.util.stream.Stream;

public interface MemberFetcher {
    Stream<Member> fetchAllByIds(List<String> ids);
}
