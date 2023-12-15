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

@Component
public class CompactionCandidateService {
	private final AllocationRepository allocationRepository;
	private final FragmentRepository fragmentRepository;


	public CompactionCandidateService(AllocationRepository allocationRepository, FragmentRepository fragmentRepository) {
		this.allocationRepository = allocationRepository;
		this.fragmentRepository = fragmentRepository;
	}

	public Map<Integer, Set<CompactionCandidate>> getCompactionTaskList(ViewCapacity viewCapacity) {
		AtomicInteger index = new AtomicInteger();
		Map<Integer, Set<CompactionCandidate>> compactionDesign = new HashMap<>();
		List<CompactionCandidate> possibleCompactionCandidates = getPossibleCompactionCandidates(viewCapacity);

		if (possibleCompactionCandidates.isEmpty()) {
			return Map.of();
		}

		StreamEx.of(possibleCompactionCandidates)
				.forPairs((ag1, ag2) -> {
					if (ag1.getFragment().isConnectedTo(ag2.getFragment())) {
						Set<CompactionCandidate> set = compactionDesign.getOrDefault(index.get(), new HashSet<>());
						var totalSum = set.stream().map(CompactionCandidate::getSize).reduce(Integer::sum).orElse(0);

						if (totalSum + ag2.getSize() <= viewCapacity.getCapacityPerPage()) {
							set.addAll(Set.of(ag1, ag2));
							compactionDesign.put(index.get(), set);
						} else {
							index.incrementAndGet();
						}
					}
				});

		return compactionDesign;
	}

	private List<CompactionCandidate> getPossibleCompactionCandidates(ViewCapacity viewCapacity) {
		return allocationRepository.getPossibleCompactionCandidates(viewCapacity.getViewName(), viewCapacity.getCapacityPerPage())
				.peek(ag -> ag.setFragment(fragmentRepository.retrieveFragment(LdesFragmentIdentifier.fromFragmentId(ag.getId()))
						.orElseThrow()))
				.filter(ag -> ag.getFragment().isImmutable())
				.filter(ag -> ag.getFragment().getDeleteTime() != null)
				.toList();
	}
}
