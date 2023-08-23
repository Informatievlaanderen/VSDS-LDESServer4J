package be.vlaanderen.informatievlaanderen.ldes.server.compaction.domain.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.LdesFragmentIdentifier;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.TreeRelation;
import be.vlaanderen.informatievlaanderen.ldes.server.fetching.entities.MemberAllocation;
import be.vlaanderen.informatievlaanderen.ldes.server.fetching.repository.AllocationRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.FragmentationStrategyImpl;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Fragment;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.FragmentRepository;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.GENERIC_TREE_RELATION;

@Service
public class CompactedFragmentCreator {
	private final FragmentRepository fragmentRepository;
	private final FragmentationStrategyImpl fragmentationStrategy;
	private final ObservationRegistry observationRegistry;
	private final AllocationRepository allocationRepository;

	public CompactedFragmentCreator(FragmentRepository fragmentRepository,
			@Qualifier("compaction-fragmentation") FragmentationStrategyImpl fragmentationStrategy,
			ObservationRegistry observationRegistry, AllocationRepository allocationRepository) {
		this.fragmentRepository = fragmentRepository;
		this.fragmentationStrategy = fragmentationStrategy;
		this.observationRegistry = observationRegistry;
		this.allocationRepository = allocationRepository;
	}

	public void createCompactedFragment(Fragment firstFragment, Fragment secondFragment,
			LdesFragmentIdentifier ldesFragmentIdentifier, int capacityPerPage) {
		List<String> membersOfCompactedFragments = getMembersOfCompactedFragments(firstFragment, secondFragment);
		if (membersOfCompactedFragments.size() < capacityPerPage) {
			Fragment compactedFragment = createAndSaveNewFragment(secondFragment, ldesFragmentIdentifier);
			updateRelationsOfPredecessorFragments(firstFragment, compactedFragment);
			addMembersOfFragmentsToCompactedFragment(membersOfCompactedFragments, compactedFragment);
		}
	}

	private List<String> getMembersOfCompactedFragments(Fragment firstFragment, Fragment secondFragment) {
		List<MemberAllocation> memberAllocationsByFragmentIdOne = allocationRepository
				.getMemberAllocationsByFragmentId(firstFragment.getFragmentIdString());
		List<MemberAllocation> memberAllocationsByFragmentIdTwo = allocationRepository
				.getMemberAllocationsByFragmentId(secondFragment.getFragmentIdString());
		return Stream.of(memberAllocationsByFragmentIdOne, memberAllocationsByFragmentIdTwo)
				.flatMap(List::stream)
				.map(MemberAllocation::getMemberId).toList();
	}

	private void addMembersOfFragmentsToCompactedFragment(List<String> membersOfCompactedFragments, Fragment fragment) {
		membersOfCompactedFragments.forEach(memberId -> {
			Observation compactionObservation = Observation.createNotStarted("compaction", observationRegistry).start();
			// memberModel can be null, since we explicitly use FragmentationStrategyImpl
			fragmentationStrategy.addMemberToFragment(fragment, memberId, null, compactionObservation);
			compactionObservation.stop();
		});
	}

	private void updateRelationsOfPredecessorFragments(Fragment firstFragment, Fragment fragment) {
		List<Fragment> predecessorFragments = fragmentRepository
				.retrieveFragmentsByOutgoingRelation(firstFragment.getFragmentId());
		predecessorFragments.forEach(predecessorFragment -> {
			predecessorFragment
					.addRelation(new TreeRelation("", fragment.getFragmentId(), "", "", GENERIC_TREE_RELATION));
			fragmentRepository.saveFragment(predecessorFragment);
		});
	}

	private Fragment createAndSaveNewFragment(Fragment secondFragment, LdesFragmentIdentifier ldesFragmentIdentifier) {
		Fragment fragment = new Fragment(ldesFragmentIdentifier, true, 0, secondFragment.getRelations());
		fragmentRepository.saveFragment(fragment);
		return fragment;
	}
}
