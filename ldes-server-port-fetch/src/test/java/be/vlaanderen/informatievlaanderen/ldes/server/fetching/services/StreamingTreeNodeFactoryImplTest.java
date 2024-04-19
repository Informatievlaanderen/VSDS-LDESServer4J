package be.vlaanderen.informatievlaanderen.ldes.server.fetching.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.MissingResourceException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.FragmentPair;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.LdesFragmentIdentifier;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.LdesFragmentRequest;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.rest.PrefixConstructor;
import be.vlaanderen.informatievlaanderen.ldes.server.fetching.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.fetching.entities.MemberAllocation;
import be.vlaanderen.informatievlaanderen.ldes.server.fetching.entities.TreeNode;
import be.vlaanderen.informatievlaanderen.ldes.server.fetching.repository.AllocationRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Fragment;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.FragmentRepository;
import org.apache.jena.rdf.model.ModelFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Optional;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.GENERATED_AT_TIME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class StreamingTreeNodeFactoryImplTest {
    private static final String COLLECTION = "collectionName";
    private static final String VIEW = "view";
    private static final ViewName VIEW_NAME = new ViewName(COLLECTION, VIEW);
    private static final String FRAGMENTATION_VALUE_1 = "2020-12-28T09:36:09.72Z";
    private FragmentRepository fragmentRepository;
    private AllocationRepository allocationRepository;
    private MemberFetcher memberFetcher;
    private StreamingTreeNodeFactory streamingTreeNodeFactory;

    @BeforeEach
    void setUp() {
        fragmentRepository = mock(FragmentRepository.class);
        allocationRepository = mock(AllocationRepository.class);
        memberFetcher = mock(MemberFetcher.class);
        streamingTreeNodeFactory = new StreamingTreeNodeFactoryImpl(fragmentRepository, allocationRepository, memberFetcher);
    }

    @Test
    void when_NoFragmentExists_ThenMissingResourceExceptionIsThrown() {
        LdesFragmentIdentifier id = new LdesFragmentIdentifier(VIEW_NAME,
                List.of(new FragmentPair(GENERATED_AT_TIME, FRAGMENTATION_VALUE_1)));
        Fragment fragment = new Fragment(id);
        Mockito.when(fragmentRepository.retrieveFragment(fragment.getFragmentId()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> streamingTreeNodeFactory.getFragmentWithoutMemberData(id))
                .isInstanceOf(MissingResourceException.class)
                .hasMessage("Resource of type: fragment with id: /collectionName/view?generatedAtTime=2020-12-28T09:36:09.72Z could not be found.");
    }

    @Test
    void when_FragmentExists_ThenReturnThatFragment() {
        LdesFragmentIdentifier id = new LdesFragmentIdentifier(VIEW_NAME,
                List.of(new FragmentPair(GENERATED_AT_TIME, FRAGMENTATION_VALUE_1)));
        Fragment fragment = new Fragment(id, true, 10, List.of(), null);
        List<Member> members = List.of(new Member("test", ModelFactory.createDefaultModel()), new Member("test2", ModelFactory.createDefaultModel()));
        TreeNode treeNode = new TreeNode(fragment.getFragmentIdString(), true, false, List.of(),
                members, "collectionName", null);

        Mockito.when(fragmentRepository.retrieveFragment(fragment.getFragmentId()))
                .thenReturn(Optional.of(fragment));
        Mockito.when(allocationRepository.getMemberAllocationsByFragmentId(fragment.getFragmentId().asDecodedFragmentId()))
                .thenReturn(members.stream().map(member -> toAllocation(member, fragment)));
        Mockito.when(memberFetcher.fetchAllByIds(List.of("test", "test2")))
                .thenReturn(members.stream());

        TreeNode returnedTreeNode = streamingTreeNodeFactory.getFragmentWithoutMemberData(id);

        assertThat(returnedTreeNode).isEqualTo(treeNode);
        assertThat(returnedTreeNode.getMembers()).isEmpty();

        List<Member> returnedMembers = streamingTreeNodeFactory.getMembersOfFragment(id.asDecodedFragmentId()).toList();

        assertThat(returnedMembers).containsAll(members);
    }

    private MemberAllocation toAllocation(Member member, Fragment fragment) {
        String id = member.id() + "/" + fragment.getFragmentIdString();
        return new MemberAllocation(id, fragment.getFragmentId().getViewName().getCollectionName()
                , fragment.getFragmentId().getViewName().getViewName(), fragment.getFragmentIdString(), member.id());
    }

}