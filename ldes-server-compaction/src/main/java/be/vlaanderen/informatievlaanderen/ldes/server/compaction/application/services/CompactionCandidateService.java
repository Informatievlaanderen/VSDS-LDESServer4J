package be.vlaanderen.informatievlaanderen.ldes.server.compaction.application.services;

import be.vlaanderen.informatievlaanderen.ldes.server.compaction.domain.entities.ViewCapacity;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.LdesFragmentIdentifier;
import be.vlaanderen.informatievlaanderen.ldes.server.fetching.entities.CompactionCandidate;
import be.vlaanderen.informatievlaanderen.ldes.server.fetching.repository.AllocationRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.FragmentRepository;
import one.util.streamex.StreamEx;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static be.vlaanderen.informatievlaanderen.ldes.server.fetching.services.CompactionCandidateComparator.sortCompactionCandidates;

@Component
public class CompactionCandidateService {
	private final AllocationRepository allocationRepository;
	private final FragmentRepository fragmentRepository;


	public CompactionCandidateService(AllocationRepository allocationRepository, FragmentRepository fragmentRepository) {
		this.allocationRepository = allocationRepository;
		this.fragmentRepository = fragmentRepository;
	}

	/**
	 * Preparation step for the Compaction Service.
	 * Will retrieve an aggregated view of the allocation table where the size of members is lower than the
	 * maximum view capacity.
	 * If these fragments are connected, these will be returned in the same group.
	 *
	 * @param viewCapacity Contains the name and capacity for a view
	 * @return a structured group of Fragments for a view that can be compacted
	 */
	public Collection<Set<CompactionCandidate>> getCompactionTaskList(ViewCapacity viewCapacity) {
		AtomicInteger index = new AtomicInteger();
		Map<Integer, Set<CompactionCandidate>> compactionDesign = new HashMap<>();
		List<CompactionCandidate> possibleCompactionCandidates = getPossibleCompactionCandidates(viewCapacity);

		if (possibleCompactionCandidates.isEmpty()) {
			return List.of();
		}

		StreamEx.of(possibleCompactionCandidates.stream())
				.forPairs((cc1, cc2) -> {
					if (cc1.getFragment().isConnectedTo(cc2.getFragment())) {
						Set<CompactionCandidate> set = compactionDesign.getOrDefault(index.get(), new HashSet<>());
						var totalSum = set.stream().map(CompactionCandidate::getSize).reduce(Integer::sum).orElse(cc1.getSize());

						if (totalSum + cc2.getSize() <= viewCapacity.getCapacityPerPage()) {
							set.addAll(Set.of(cc1, cc2));
							compactionDesign.put(index.get(), set);
						} else {
							index.incrementAndGet();
						}
					}
				});

		return compactionDesign.values();
	}

	private List<CompactionCandidate> getPossibleCompactionCandidates(ViewCapacity viewCapacity) {
		var compactionCandidates = allocationRepository.getPossibleCompactionCandidates(viewCapacity.getViewName(), viewCapacity.getCapacityPerPage())
				.toList();
		compactionCandidates.forEach(cc -> cc.setFragment(fragmentRepository.retrieveFragment(LdesFragmentIdentifier.fromFragmentId(cc.getId()))
				.orElseThrow()));
		return sortCompactionCandidates(compactionCandidates.stream()
				.filter(cc -> cc.getFragment().isImmutable())
				.filter(cc -> cc.getFragment().getDeleteTime() == null))
				.toList();
	}
}
