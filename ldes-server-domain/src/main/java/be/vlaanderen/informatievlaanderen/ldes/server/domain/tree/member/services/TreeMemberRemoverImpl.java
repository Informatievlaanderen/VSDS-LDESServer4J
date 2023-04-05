package be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.repository.MemberRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.memberreferences.entities.MemberReferencesRepository;
import io.micrometer.core.instrument.Metrics;
import org.springframework.stereotype.Component;

@Component
public class TreeMemberRemoverImpl implements TreeMemberRemover {

	private final MemberReferencesRepository memberReferencesRepository;
	private final MemberRepository memberRepository;

	public TreeMemberRemoverImpl(MemberReferencesRepository memberReferencesRepository,
			MemberRepository memberRepository) {
		this.memberReferencesRepository = memberReferencesRepository;
		this.memberRepository = memberRepository;
	}

	public void tryRemovingMember(String memberId) {
		if (!memberReferencesRepository.hasMemberReferences(memberId)) {
			memberRepository.deleteMember(memberId);
			memberReferencesRepository.deleteMemberReference(memberId);
			Metrics.counter("ldes_server_deleted_members_count").increment();
		}
	}
}
