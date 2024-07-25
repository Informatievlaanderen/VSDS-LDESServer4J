package be.vlaanderen.informatievlaanderen.ldes.server.compaction.application.services;

import be.vlaanderen.informatievlaanderen.ldes.server.compaction.domain.entities.CompactedFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.fetching.entities.CompactionCandidate;
import be.vlaanderen.informatievlaanderen.ldes.server.fetching.entities.TreeNode;
import be.vlaanderen.informatievlaanderen.ldes.server.fetching.repository.TreeMemberRepository;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class PaginationCompactionService {
	private final TreeMemberRepository treeMemberRepository;
	private final ApplicationEventPublisher applicationEventPublisher;
	private final ObservationRegistry observationRegistry;
	private final ApplicationEventPublisher eventPublisher;

	public PaginationCompactionService(TreeMemberRepository treeMemberRepository,
	                                   ApplicationEventPublisher applicationEventPublisher, ObservationRegistry observationRegistry, ApplicationEventPublisher eventPublisher) {
		this.treeMemberRepository = treeMemberRepository;
		this.applicationEventPublisher = applicationEventPublisher;
		this.observationRegistry = observationRegistry;
		this.eventPublisher = eventPublisher;
	}

	public void applyCompactionForFragments(Set<CompactionCandidate> toBeCompactedFragments) {
		Observation compactionObservation = Observation.createNotStarted("compaction", observationRegistry).start();
		CompactedFragment compacted = new CompactedFragment(toBeCompactedFragments);

		TreeNode compactedFragment = compacted.getFragment();

//		fragmentRepository.saveFragment(compactedFragment);
//
//		var membersOfCompactedFragments =
//				allocationRepository.getMemberAllocationIdsByFragmentIds(compacted.getImpactedFragmentIds());
//
//		treeMemberRepository.findAllIdsByTreeNodeIds(compacted.getImpactedFragmentIds())
//
//		updateRelationsOfPredecessorFragments(compactedFragment, compacted.getFirstImpactedFragment());
//		addMembersOfFragmentsToCompactedFragment(membersOfCompactedFragments, compactedFragment);
//		applicationEventPublisher.publishEvent(new FragmentsCompactedEvent(compacted.getImpactedFragmentIdentifiers()));
		compactionObservation.stop();
	}

//	private void addMembersOfFragmentsToCompactedFragment(List<String> membersOfCompactedFragments, Fragment fragment) {
//		eventPublisher.publishEvent(
//				new BulkMemberAllocatedEvent(membersOfCompactedFragments, fragment.getViewName().getCollectionName(),
//						fragment.getViewName().getViewName(), fragment.getFragmentIdString()));
//
//		fragmentRepository.incrementNrOfMembersAdded(fragment.getFragmentId(), membersOfCompactedFragments.size());
//	}
//
//	private void updateRelationsOfPredecessorFragments(Fragment compactedFragment, Fragment firstlyCompactedFragment) {
//		List<Fragment> predecessorFragments = fragmentRepository
//				.retrieveFragmentsByOutgoingRelation(firstlyCompactedFragment.getFragmentId());
//		predecessorFragments.forEach(predecessorFragment -> {
//			predecessorFragment
//					.addRelation(new TreeRelation("", compactedFragment.getFragmentId(), "", "", GENERIC_TREE_RELATION));
//			predecessorFragment.removeRelationToIdentifier(firstlyCompactedFragment.getFragmentId());
//			fragmentRepository.saveFragment(predecessorFragment);
//		});
//	}
}
