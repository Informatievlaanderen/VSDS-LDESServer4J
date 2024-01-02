package be.vlaanderen.informatievlaanderen.ldes.server.fetching.repository;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.fetching.entities.CompactionCandidate;
import be.vlaanderen.informatievlaanderen.ldes.server.fetching.entities.MemberAllocation;

import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

public interface AllocationRepository {
	void saveAllocation(MemberAllocation memberAllocation);
	void saveAllocations(List<MemberAllocation> memberAllocations);

	List<MemberAllocation> getMemberAllocationsByFragmentId(String fragmentId);

	List<String> getMemberAllocationIdsByFragmentIds(Set<String> fragmentIds);

	void deleteByMemberIdAndCollectionNameAndViewName(String memberId, String collectionName, String viewName);

	void deleteByCollectionName(String collectionName);

	void deleteByCollectionNameAndViewName(String collectionName, String viewName);

	void deleteAllByFragmentId(Set<String> fragmentIds);

	Stream<CompactionCandidate> getPossibleCompactionCandidates(ViewName viewName, int capacityPerPage);
}
