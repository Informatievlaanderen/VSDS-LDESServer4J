package be.vlaanderen.informatievlaanderen.ldes.server.snapshot.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Fragment;

import java.util.List;
import java.util.Map;

public interface MemberCollector {

	Map<String, List<Member>> getMembersGroupedByVersionOf(List<Fragment> fragments);
}
