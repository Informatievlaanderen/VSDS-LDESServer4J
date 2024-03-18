package be.vlaanderen.informatievlaanderen.ldes.server.fetching.services;

import be.vlaanderen.informatievlaanderen.ldes.server.fetching.entities.Member;

import java.util.List;

public interface MemberFetcher {
    List<Member> fetchAllByIds(List<String> ids);
}
