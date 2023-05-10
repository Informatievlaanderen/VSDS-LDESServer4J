package be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.repository.MemberRepository;
import io.micrometer.core.instrument.Metrics;
import org.springframework.stereotype.Component;

@Component
public class TreeMemberRemoverImpl implements TreeMemberRemover {

	private final MemberRepository memberRepository;

	public TreeMemberRemoverImpl(MemberRepository memberRepository) {
		this.memberRepository = memberRepository;
	}

	public void deletingMemberFromCollection(String memberId) {
		memberRepository
				.getMember(memberId)
				.filter(member -> member.getTreeNodeReferences().isEmpty())
				.ifPresent(member -> {
					memberRepository.deleteMember(memberId);
					Metrics.counter("ldes_server_deleted_members_count").increment();
				});
	}
}
