package be.vlaanderen.informatievlaanderen.ldes.server.fetchapplication.integrationtest.stub;

import be.vlaanderen.informatievlaanderen.ldes.server.fetchdomain.entities.MemberAllocation;
import be.vlaanderen.informatievlaanderen.ldes.server.fetchdomain.repository.AllocationRepository;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class InMemoryAllocationRepository implements AllocationRepository {

	private final ConcurrentHashMap<String, MemberAllocation> memberAllocations;

	public InMemoryAllocationRepository() {
		this.memberAllocations = new ConcurrentHashMap<>();
	}

	@Override
	public void saveAllocation(MemberAllocation memberAllocation) {
		memberAllocations.put(memberAllocation.getId(), memberAllocation);
	}

	@Override
	public List<MemberAllocation> getMemberAllocationsByFragmentId(String fragmentId) {
		return memberAllocations.values()
				.stream()
				.filter(memberAllocation -> memberAllocation.getFragmentId().equals(fragmentId))
				.sorted(Comparator.comparing(MemberAllocation::getMemberId))
				.toList();
	}

	@Override
	public void deleteByMemberIdAndCollectionNameAndViewName(String memberId, String collectionName, String viewName) {
		List<String> idsToRemove = memberAllocations.values()
				.stream()
				.filter(memberAllocation -> memberAllocation.getMemberId().equals(memberId)
						&& memberAllocation.getCollectionName().equals(collectionName)
						&& memberAllocation.getViewName().equals(viewName))
				.map(MemberAllocation::getId)
				.toList();
		idsToRemove.forEach(memberAllocations::remove);
	}

	@Override
	public void deleteByCollectionName(String collectionName) {
		List<String> idsToRemove = memberAllocations.values()
				.stream()
				.filter(memberAllocation -> memberAllocation.getCollectionName().equals(collectionName))
				.map(MemberAllocation::getId)
				.toList();
		idsToRemove.forEach(memberAllocations::remove);
	}

	@Override
	public void deleteByCollectionNameAndViewName(String collectionName, String viewName) {
		List<String> idsToRemove = memberAllocations.values()
				.stream()
				.filter(memberAllocation -> memberAllocation.getCollectionName().equals(collectionName)
						&& memberAllocation.getViewName().equals(viewName))
				.map(MemberAllocation::getId)
				.toList();
		idsToRemove.forEach(memberAllocations::remove);
	}
}
