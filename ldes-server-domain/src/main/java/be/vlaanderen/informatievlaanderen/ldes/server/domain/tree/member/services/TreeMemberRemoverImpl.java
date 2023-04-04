package be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.repository.MemberRepository;
import io.micrometer.core.instrument.Metrics;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class TreeMemberRemoverImpl implements TreeMemberRemover {

	private final MemberRepository memberRepository;

	public TreeMemberRemoverImpl(MemberRepository memberRepository) {
		this.memberRepository = memberRepository;
	}

	public void tryRemovingMember(String memberId) {
		Optional<Member> member = memberRepository.getMember(memberId);
		if (member.isPresent() && member.get().getTreeNodeReferences().isEmpty()) {
			memberRepository.deleteMember(memberId);
			Metrics.counter("ldes_server_deleted_members_count").increment();
		}
	}
}
