package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.retentionpolicy.RetentionPolicy;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.LdesFragmentIdentifier;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.repository.MemberRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.services.TreeMemberRemover;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@Component
public class TreeNodeRemoverImpl implements TreeNodeRemover {

	private final LdesFragmentRepository ldesFragmentRepository;
	private final MemberRepository memberRepository;
	private final TreeMemberRemover treeMemberRemover;
	private final RetentionPolicyCollection retentionPolicyCollection;

	public TreeNodeRemoverImpl(LdesFragmentRepository ldesFragmentRepository,
			MemberRepository memberRepository,
			TreeMemberRemover treeMemberRemover,
			RetentionPolicyCollection retentionPolicyCollection) {
		this.ldesFragmentRepository = ldesFragmentRepository;
		this.memberRepository = memberRepository;
		this.treeMemberRemover = treeMemberRemover;
		this.retentionPolicyCollection = retentionPolicyCollection;
	}

	@Scheduled(fixedDelay = 10000)
	public void removeTreeNodeMembers() {
		retentionPolicyCollection
				.getRetentionPolicyMap()
				.entrySet()
				.stream()
				.filter(this::viewHasRetentionPolicies)
				.forEach(viewWithRetentionPolicies -> removeMembersFromViewThatMatchRetentionPolicies(
						viewWithRetentionPolicies.getKey(), viewWithRetentionPolicies.getValue()));
	}

	@Override
	public void removeLdesFragmentsOfView(ViewName viewName) {
		memberRepository.removeViewReferences(viewName);
		ldesFragmentRepository.removeLdesFragmentsOfView(viewName.asString());
	}

	@Override
	public void deleteTreeNodesByCollection(String collectionName) {
		ldesFragmentRepository.deleteTreeNodesByCollection(collectionName);
	}

	private boolean viewHasRetentionPolicies(Map.Entry<ViewName, List<RetentionPolicy>> entry) {
		return !entry.getValue().isEmpty();
	}

	private void removeMembersFromViewThatMatchRetentionPolicies(ViewName view,
			List<RetentionPolicy> retentionPoliciesOfView) {
		ldesFragmentRepository
				.retrieveFragmentsOfView(view.asString())
				.forEach(ldesFragmentOfView -> removeMembersFromFragmentOfViewThatMatchRetentionPolicies(
						retentionPoliciesOfView, ldesFragmentOfView.getFragmentId()));
	}

	private void removeMembersFromFragmentOfViewThatMatchRetentionPolicies(
			List<RetentionPolicy> retentionPoliciesOfView, LdesFragmentIdentifier ldesFragmentId) {
		Stream<Member> membersOfFragment = memberRepository
				.getMembersByReference(ldesFragmentId.asString());
		membersOfFragment
				.filter(member -> memberMatchesAllRetentionPoliciesOfView(retentionPoliciesOfView, member))
				.forEach(member -> removeMemberFromFragmentOfViewAndTryDeletingMember(ldesFragmentId, member));
	}

	private void removeMemberFromFragmentOfViewAndTryDeletingMember(LdesFragmentIdentifier ldesFragmentId,
			Member member) {
		memberRepository.removeMemberReference(member.getLdesMemberId(),
				ldesFragmentId.asString());
		treeMemberRemover.deletingMemberFromCollection(member.getLdesMemberId());
	}

	private boolean memberMatchesAllRetentionPoliciesOfView(List<RetentionPolicy> retentionPolicies, Member member) {
		return retentionPolicies
				.stream()
				.allMatch(retentionPolicy -> retentionPolicy.matchesPolicy(member));
	}

}
