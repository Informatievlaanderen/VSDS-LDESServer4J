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

	public boolean tryRemovingMember(String memberId) {
		if (memberRepository.deleteMember(memberId)) {
			Metrics.counter("ldes_server_deleted_members_count").increment();
			return true;
		}
		return false;
	}
}
