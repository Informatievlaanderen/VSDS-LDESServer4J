package be.vlaanderen.informatievlaanderen.ldes.server.snapshot.services;

import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Fragment;
import be.vlaanderen.informatievlaanderen.ldes.server.snapshot.entities.Member;

import java.util.List;
import java.util.Map;

public interface MemberCollector {

	Map<String, List<Member>> getMembersGroupedByVersionOf(List<Fragment> fragments);
}
