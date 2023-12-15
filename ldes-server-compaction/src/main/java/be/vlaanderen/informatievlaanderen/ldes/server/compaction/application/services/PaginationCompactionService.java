package be.vlaanderen.informatievlaanderen.ldes.server.compaction.application.services;

import be.vlaanderen.informatievlaanderen.ldes.server.compaction.domain.entities.CompactedFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.compaction.FragmentsCompactedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.TreeRelation;
import be.vlaanderen.informatievlaanderen.ldes.server.fetching.entities.AllocationAggregate;
import be.vlaanderen.informatievlaanderen.ldes.server.fetching.repository.AllocationRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.FragmentationStrategy;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Fragment;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.FragmentRepository;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.GENERIC_TREE_RELATION;

@Component
public class PaginationCompactionService {
	private final FragmentRepository fragmentRepository;
	private final AllocationRepository allocationRepository;
	private final FragmentationStrategy fragmentationStrategy;
	private final ApplicationEventPublisher applicationEventPublisher;
	private final ObservationRegistry observationRegistry;

	public PaginationCompactionService(FragmentRepository fragmentRepository,
	                                   AllocationRepository allocationRepository,
	                                   @Qualifier("compaction-fragmentation") FragmentationStrategy fragmentationStrategy,
	                                   ApplicationEventPublisher applicationEventPublisher, ObservationRegistry observationRegistry) {
		this.fragmentRepository = fragmentRepository;
		this.allocationRepository = allocationRepository;
		this.fragmentationStrategy = fragmentationStrategy;
		this.applicationEventPublisher = applicationEventPublisher;
		this.observationRegistry = observationRegistry;

	}

	public void applyCompactionForFragments(Set<AllocationAggregate> toBeCompactedFragments) {
		Observation compactionObservation = Observation.createNotStarted("compaction", observationRegistry).start();
		CompactedFragment compacted = new CompactedFragment(toBeCompactedFragments);

		Fragment compactedFragment = compacted.getFragment();

		fragmentRepository.saveFragment(compactedFragment);

		var membersOfCompactedFragments =
				allocationRepository.getMemberAllocationIdsByFragmentIds(compacted.getImpactedFragmentIds());

		updateRelationsOfPredecessorFragments(compactedFragment, compacted.getFirstImpactedFragment());
		addMembersOfFragmentsToCompactedFragment(membersOfCompactedFragments, compactedFragment, compactionObservation);
		applicationEventPublisher.publishEvent(new FragmentsCompactedEvent(compacted.getImpactedFragmentIdentifiers()));
		compactionObservation.stop();
	}

	private void addMembersOfFragmentsToCompactedFragment(List<String> membersOfCompactedFragments, Fragment fragment,
	                                                      Observation compactionObservation) {
		membersOfCompactedFragments.forEach(memberId -> {
			// memberModel can be null, since we explicitly use FragmentationStrategyImpl
			fragmentationStrategy.addMemberToFragment(fragment, memberId, null, compactionObservation);
		});
	}

	private void updateRelationsOfPredecessorFragments(Fragment compactedFragment, Fragment firstlyCompactedFragment) {
		List<Fragment> predecessorFragments = fragmentRepository
				.retrieveFragmentsByOutgoingRelation(firstlyCompactedFragment.getFragmentId());
		predecessorFragments.forEach(predecessorFragment -> {
			predecessorFragment
					.addRelation(new TreeRelation("", compactedFragment.getFragmentId(), "", "", GENERIC_TREE_RELATION));
			predecessorFragment.removeRelationToIdentifier(firstlyCompactedFragment.getFragmentId());
			fragmentRepository.saveFragment(predecessorFragment);
		});
	}
}
