package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;

public interface AllocationRepository {
	void allocateMemberToFragment(String memberId, ViewName viewName, String fragmentId);
	void unallocateMemberFromView(String memberId, ViewName viewName);
}
