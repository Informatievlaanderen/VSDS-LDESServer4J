package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.retentionpolicy.RetentionPolicy;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.repository.MemberRepository;
import io.micrometer.core.instrument.Metrics;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class TreeNodeRemoverImpl implements TreeNodeRemover {

	private final LdesFragmentRepository ldesFragmentRepository;
	private final Map<String, List<RetentionPolicy>> retentionPolicyMap;
	private final MemberRepository memberRepository;
	private final ParentUpdater parentUpdater;

	public TreeNodeRemoverImpl(LdesFragmentRepository ldesFragmentRepository,
			Map<String, List<RetentionPolicy>> retentionPolicyMap,
			MemberRepository memberRepository,
			ParentUpdater parentUpdater) {
		this.ldesFragmentRepository = ldesFragmentRepository;
		this.retentionPolicyMap = retentionPolicyMap;
		this.memberRepository = memberRepository;
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
						ldesFragmentRepository.setSoftDeleted(ldesFragment);
						parentUpdater.updateParent(ldesFragment);

						memberRepository.removeMemberReferencesForFragment(ldesFragment);
						long deletedCount = memberRepository.removeMembersWithNoReferences();

						for (int i=0; i<deletedCount; i++){
							Metrics.counter("ldes_server_deleted_members_count").increment();
						}
					});
				});
	}

}
