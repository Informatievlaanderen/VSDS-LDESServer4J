package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;

import java.util.stream.Stream;

public interface AllocationRepository {
	void allocateMemberToFragment(String memberId, ViewName viewName, String fragmentId);

	void unallocateMemberFromView(String memberId, ViewName viewName);

	void unallocateAllMembersFromView(ViewName viewName);

	void unallocateMembersFromCollection(String collectionName);

	Stream<String> findMembersForFragment(String fragmentId);
}
