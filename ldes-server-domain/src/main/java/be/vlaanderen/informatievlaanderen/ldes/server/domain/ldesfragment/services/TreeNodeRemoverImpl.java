package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.retentionpolicy.RetentionPolicy;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.retentionpolicy.creation.RetentionPolicyCreator;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.repository.MemberRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.services.TreeMemberRemover;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.view.valueobject.ViewAddedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.view.valueobject.ViewDeletedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class TreeNodeRemoverImpl implements TreeNodeRemover {

	private final LdesFragmentRepository ldesFragmentRepository;
	private final MemberRepository memberRepository;
	private final Map<ViewName, List<RetentionPolicy>> retentionPolicyMap;
	private final TreeMemberRemover treeMemberRemover;
	private final ParentUpdater parentUpdater;
	private final RetentionPolicyCreator retentionPolicyCreator;

	// TODO when the definiton of views in config is going to be deprecated, the
	// retentionPolicyMap should no longer be injected.
	// But start from an empty Map and be filled via ViewAddedEvents.
	public TreeNodeRemoverImpl(LdesFragmentRepository ldesFragmentRepository,
			MemberRepository memberRepository, Map<ViewName, List<RetentionPolicy>> retentionPolicyMap,
			TreeMemberRemover treeMemberRemover,
			ParentUpdater parentUpdater, RetentionPolicyCreator retentionPolicyCreator) {
		this.ldesFragmentRepository = ldesFragmentRepository;
		this.memberRepository = memberRepository;
		this.retentionPolicyMap = retentionPolicyMap;
		this.treeMemberRemover = treeMemberRemover;
		this.parentUpdater = parentUpdater;
		this.retentionPolicyCreator = retentionPolicyCreator;
	}

	@Scheduled(fixedDelay = 10000)
	public void removeTreeNodes() {
		retentionPolicyMap
				.entrySet()
				.stream()
				.filter(stringListEntry -> !stringListEntry.getValue().isEmpty())
				.forEach(entry -> {
					ViewName view = entry.getKey();
					List<RetentionPolicy> retentionPolicies = entry.getValue();
					List<LdesFragment> ldesFragments = ldesFragmentRepository
							.retrieveNonDeletedImmutableFragmentsOfView(view.asString())
							.filter(ldesFragment -> retentionPolicies
									.stream()
									.allMatch(retentionPolicy -> retentionPolicy.matchesPolicy(ldesFragment)))
							.toList();
					ldesFragments.forEach(ldesFragment -> {
						ldesFragment.setSoftDeleted(true);
						ldesFragmentRepository.saveFragment(ldesFragment);
						parentUpdater.updateParent(ldesFragment);
						memberRepository
								.getMembersByReference(ldesFragment.getFragmentId())
								.map(Member::getLdesMemberId)
								.forEach(memberId -> {
									memberRepository.removeMemberReference(memberId,
											ldesFragment.getFragmentId());
									treeMemberRemover.tryRemovingMember(memberId);
								});
					});
				});
	}

	@EventListener
	public void handleViewAddedEvent(ViewAddedEvent event) {
		retentionPolicyMap.put(event.getViewName(),
				retentionPolicyCreator.createRetentionPolicyListForView(event.getViewSpecification()));
	}

	@EventListener
	public void handleViewDeletedEvent(ViewDeletedEvent event) {
		retentionPolicyMap.remove(event.getViewName());
	}

}
