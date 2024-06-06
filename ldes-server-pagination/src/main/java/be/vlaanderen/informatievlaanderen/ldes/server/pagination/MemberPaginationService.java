package be.vlaanderen.informatievlaanderen.ldes.server.pagination;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.LdesFragmentIdentifier;
import be.vlaanderen.informatievlaanderen.ldes.server.fetching.entities.MemberAllocation;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.BucketisedMember;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Fragment;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.FragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.pagination.services.OpenPageProvider;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class MemberPaginationService {
	private final FragmentRepository fragmentRepository;
	private final OpenPageProvider pageProvider;
	private final int maxPageSize;

	public MemberPaginationService(FragmentRepository fragmentRepository,
	                               OpenPageProvider openPageProvider, int maxPageSize) {
		this.fragmentRepository = fragmentRepository;
		this.pageProvider = openPageProvider;
		this.maxPageSize = maxPageSize;
	}

	public List<MemberAllocation> paginateMember(List<BucketisedMember> bucketisedMembers) {
		if (bucketisedMembers.isEmpty()) {
			return null;
		}

		String viewName = bucketisedMembers.getFirst().getViewName();
		String bucketId = bucketisedMembers.getFirst().fragmentId();

		Map<LdesFragmentIdentifier, AtomicInteger> fragmentMemberCounter = new HashMap<>();

		AtomicReference<Fragment> activePage = new AtomicReference<>(pageProvider
				.retrieveOpenFragmentOrCreateNewFragment(LdesFragmentIdentifier.fromFragmentId(bucketId)));
		AtomicInteger freeItems = new AtomicInteger(maxPageSize - activePage.get().getNrOfMembersAdded());

		List<MemberAllocation> memberAllocations = bucketisedMembers.stream()
				.map(bucketisedMember -> {
					String id = bucketisedMember.memberId() + "/" + activePage.get().getFragmentIdString();
					MemberAllocation memberAllocation = new MemberAllocation(id, activePage.get().getViewName().getCollectionName(),
							viewName, activePage.get().getFragmentIdString(), bucketisedMember.memberId());

					if (freeItems.get() == 0) {
						activePage.set(pageProvider
								.retrieveOpenFragmentOrCreateNewFragment(LdesFragmentIdentifier.fromFragmentId(bucketId)));
						freeItems.set(maxPageSize - activePage.get().getNrOfMembersAdded());
					}
					fragmentMemberCounter.putIfAbsent(activePage.get().getFragmentId(), new AtomicInteger(0));
					fragmentMemberCounter.get(activePage.get().getFragmentId()).incrementAndGet();
					return memberAllocation;
				})
				.toList();

		fragmentMemberCounter.forEach((key, value) -> fragmentRepository.incrementNrOfMembersAdded(key, value.get()));

		return memberAllocations;
	}

	protected int getMaxPageSize() {
		return maxPageSize;
	}
}
