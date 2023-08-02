package be.vlaanderen.informatievlaanderen.ldes.server.fetching.repository;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;

import java.util.List;

public interface AllocationRepository {
	void allocateMemberToFragment(String memberId, ViewName viewName, String fragmentId);

	void unallocateMemberFromView(String memberId, ViewName viewName);

	void unallocateAllMembersFromView(ViewName viewName);

	void unallocateMembersFromCollection(String collectionName);

	List<String> findMemberIdsForFragment(String fragmentId);
}
