package be.vlaanderen.informatievlaanderen.ldes.server.fetching.repository;

import be.vlaanderen.informatievlaanderen.ldes.server.fetching.entities.MemberAllocation;

import java.util.List;

public interface AllocationRepository {
	void saveAllocation(MemberAllocation memberAllocation);

	List<MemberAllocation> getMemberAllocationsByFragmentId(String fragmentId);

	void deleteByMemberIdAndCollectionNameAndViewName(String memberId, String collectionName, String viewName);

	void deleteByCollectionName(String collectionName);

	void deleteByCollectionNameAndViewName(String collectionName, String viewName);
}
