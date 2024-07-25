package be.vlaanderen.informatievlaanderen.ldes.server.fetching.repository;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.fetching.entities.CompactionCandidate;

import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

public interface AllocationRepository {

	List<String> getMemberAllocationIdsByFragmentIds(Set<String> fragmentIds);

	Stream<CompactionCandidate> getPossibleCompactionCandidates(ViewName viewName, int capacityPerPage);
}
