package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.snapshot.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.LdesFragmentIdentifier;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.repository.MemberRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class MemberCollectorImpl implements MemberCollector {

	private final MemberRepository memberRepository;

	public MemberCollectorImpl(MemberRepository memberRepository) {
		this.memberRepository = memberRepository;
	}

	@Override
	public Map<String, List<Member>> getMembersGroupedByVersionOf(List<LdesFragment> ldesFragments) {
		return ldesFragments.stream()
				.map(LdesFragment::getFragmentId)
				.map(LdesFragmentIdentifier::asString)
				.flatMap(memberRepository::getMembersByReference)
				.filter(new SnapshotValidPredicate())
				.collect(Collectors.groupingBy(Member::getVersionOf));
	}
}
