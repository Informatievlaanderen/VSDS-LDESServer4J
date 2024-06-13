package be.vlaanderen.informatievlaanderen.ldes.server.pagination;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.LdesFragmentIdentifier;
import be.vlaanderen.informatievlaanderen.ldes.server.fetching.entities.MemberAllocation;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.BucketisedMember;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Fragment;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.FragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.pagination.services.OpenPageProvider;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

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

	@Transactional
	public List<MemberAllocation> paginateMember(List<BucketisedMember> bucketisedMembers) {
		if (bucketisedMembers.isEmpty()) {
			return List.of();
		}

		String viewName = bucketisedMembers.getFirst().getViewName();
		String bucketId = bucketisedMembers.getFirst().fragmentId();

		Set<Fragment> updatedPages = new HashSet<>();

		AtomicReference<Fragment> activePage = new AtomicReference<>(pageProvider
				.retrieveOpenFragmentOrCreateNewFragment(LdesFragmentIdentifier.fromFragmentId(bucketId)));

		Set<MemberAllocation> memberAllocations = bucketisedMembers.stream()
				.map(bucketisedMember -> {
					if (pageProvider.getMemberLimit() <= activePage.get().getNrOfMembersAdded()) {
						fragmentRepository.saveFragment(activePage.get());
						updatedPages.remove(activePage.get());
						activePage.set(pageProvider
								.retrieveOpenFragmentOrCreateNewFragment(LdesFragmentIdentifier.fromFragmentId(bucketId)));
					}

					String id = bucketisedMember.memberId() + "/" + activePage.get().getFragmentIdString();
					MemberAllocation memberAllocation = new MemberAllocation(id, activePage.get().getViewName().getCollectionName(),
							viewName, activePage.get().getFragmentIdString(), bucketisedMember.memberId());

					activePage.get().incrementNrOfMembersAdded();
					updatedPages.add(activePage.get());
					return memberAllocation;
				})
				.collect(Collectors.toSet());

		updatedPages.forEach(fragmentRepository::saveFragment);

		return memberAllocations.stream().toList();
	}

	protected int getMaxPageSize() {
		return maxPageSize;
	}
}
