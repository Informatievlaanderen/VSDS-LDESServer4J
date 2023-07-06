package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.snapshot.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.entities.Member;

import java.util.List;
import java.util.Map;

public interface MemberCollector {

	Map<String, List<Member>> getMembersGroupedByVersionOf(List<LdesFragment> ldesFragments);
}
