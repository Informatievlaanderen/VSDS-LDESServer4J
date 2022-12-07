package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.retentionpolicy.RetentionPolicy;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.services.TreeMemberRemover;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.memberreferences.entities.MemberReferencesRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class TreeNodeRemoverImpl implements TreeNodeRemover {

	private final LdesFragmentRepository ldesFragmentRepository;
	private final Map<String, List<RetentionPolicy>> retentionPolicyMap;
	private final MemberReferencesRepository memberReferencesRepository;
	private final TreeMemberRemover treeMemberRemover;
	private final ParentUpdater parentUpdater;

	public TreeNodeRemoverImpl(LdesFragmentRepository ldesFragmentRepository,
			Map<String, List<RetentionPolicy>> retentionPolicyMap,
			MemberReferencesRepository memberReferencesRepository, TreeMemberRemover treeMemberRemover,
			ParentUpdater parentUpdater) {
		this.ldesFragmentRepository = ldesFragmentRepository;
		this.retentionPolicyMap = retentionPolicyMap;
		this.memberReferencesRepository = memberReferencesRepository;
		this.treeMemberRemover = treeMemberRemover;
		this.parentUpdater = parentUpdater;
	}

	@Scheduled(fixedDelay = 10000)
	public void removeTreeNodes() {
		retentionPolicyMap
				.entrySet()
				.stream()
				.filter(stringListEntry -> !stringListEntry.getValue().isEmpty())
				.forEach(entry -> {
					String view = entry.getKey();
					List<RetentionPolicy> retentionPolicies = entry.getValue();
					List<LdesFragment> ldesFragments = ldesFragmentRepository
							.retrieveNonDeletedImmutableFragmentsOfView(view)
							.filter(ldesFragment -> retentionPolicies
									.stream()
									.allMatch(retentionPolicy -> retentionPolicy.matchesPolicy(ldesFragment)))
							.toList();
					ldesFragments.forEach(ldesFragment -> {
						ldesFragment.setSoftDeleted(true);
						ldesFragmentRepository.saveFragment(ldesFragment);
						parentUpdater.updateParent(ldesFragment);
						// TODO fix
						// ldesFragment
						// .getMemberIds()
						// .forEach(memberId -> {
						// memberReferencesRepository.removeMemberReference(memberId,
						// ldesFragment.getFragmentId());
						// treeMemberRemover.tryRemovingMember(memberId);
						// });
					});
				});
	}

}
