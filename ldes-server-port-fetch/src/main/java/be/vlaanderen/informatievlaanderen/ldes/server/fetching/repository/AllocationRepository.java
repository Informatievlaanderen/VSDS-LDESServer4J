package be.vlaanderen.informatievlaanderen.ldes.server.fetching.repository;

import be.vlaanderen.informatievlaanderen.ldes.server.fetching.entities.MemberAllocation;

import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

public interface AllocationRepository {
	void saveAllocation(MemberAllocation memberAllocation);

	List<MemberAllocation> getMemberAllocationsByFragmentId(String fragmentId);

	List<String> getMemberAllocationIdsByFragmentIds(Set<String> fragmentIds);

	long countByCollectionNameAndViewName(String collectionName, String viewName);

	void deleteByMemberIdAndCollectionNameAndViewName(String memberId, String collectionName, String viewName);

	void deleteByCollectionName(String collectionName);

	void deleteByCollectionNameAndViewName(String collectionName, String viewName);

	void deleteByFragmentId(String fragmentId);
	void deleteAllByFragmentId(Set<String> fragmentIds);
}
