package be.vlaanderen.informatievlaanderen.ldes.server.snapshot.services;

import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Fragment;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.repositories.MemberRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.snapshot.entities.Member;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@SuppressWarnings({ "java:S125", "java:S1068" })
@Component
public class MemberCollectorImpl implements MemberCollector {

	private final MemberRepository memberRepository;

	public MemberCollectorImpl(MemberRepository memberRepository) {
		this.memberRepository = memberRepository;
	}

	@Override
	public Map<String, List<Member>> getMembersGroupedByVersionOf(List<Fragment> fragments) {
		throw new NotImplementedException("To be implemented with snapshot member repository");
		// return fragments.stream()
		// .map(Fragment::getFragmentId)
		// .map(LdesFragmentIdentifier::asString)
		// .flatMap(memberRepository::getMembersByReference)
		// .filter(new SnapshotValidPredicate())
		// .collect(Collectors.groupingBy(Member::versionOf));
	}
}
