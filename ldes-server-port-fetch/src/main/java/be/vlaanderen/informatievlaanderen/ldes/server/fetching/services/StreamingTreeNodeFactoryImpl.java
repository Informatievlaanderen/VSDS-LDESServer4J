package be.vlaanderen.informatievlaanderen.ldes.server.fetching.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.MissingResourceException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.LdesFragmentIdentifier;
import be.vlaanderen.informatievlaanderen.ldes.server.fetching.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.fetching.entities.MemberAllocation;
import be.vlaanderen.informatievlaanderen.ldes.server.fetching.entities.TreeNode;
import be.vlaanderen.informatievlaanderen.ldes.server.fetching.repository.AllocationRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.FragmentRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Stream;

@Component
public class StreamingTreeNodeFactoryImpl implements StreamingTreeNodeFactory {

    private final FragmentRepository fragmentRepository;
    private final AllocationRepository allocationRepository;
    private final MemberFetcher memberFetcher;

    public StreamingTreeNodeFactoryImpl(FragmentRepository fragmentRepository, AllocationRepository allocationRepository,
                                        MemberFetcher memberFetcher) {
        this.fragmentRepository = fragmentRepository;
        this.allocationRepository = allocationRepository;
        this.memberFetcher = memberFetcher;
    }

    @Override
    public TreeNode getFragmentWithoutMemberData(LdesFragmentIdentifier treeNodeId) {
        return fragmentRepository.retrieveFragment(treeNodeId).map(fragment -> new TreeNode(fragment.getFragmentIdString(), fragment.isImmutable(), fragment.isRoot(),
                        fragment.getRelations(), List.of(), fragment.getFragmentId().getViewName().getCollectionName(), fragment.getNextUpdateTs()))
                .orElseThrow(
                        () -> new MissingResourceException("fragment", treeNodeId.asDecodedFragmentId()));
    }

    @Override
    public Stream<Member> getMembersOfFragment(String treeNodeId) {
        List<String> memberIds = allocationRepository.getMemberAllocationsByFragmentId(treeNodeId).map(MemberAllocation::getMemberId).toList();
        return memberFetcher.fetchAllByIds(memberIds);
    }
}
