package be.vlaanderen.informatievlaanderen.ldes.server.fetching.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.MissingResourceException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.fetching.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.fetching.entities.TreeNode;
import be.vlaanderen.informatievlaanderen.ldes.server.fetching.repository.TreeMemberRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.fetching.repository.TreeNodeRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.fetching.valueobjects.FragmentPair;
import be.vlaanderen.informatievlaanderen.ldes.server.fetching.valueobjects.LdesFragmentIdentifier;
import org.apache.jena.rdf.model.ModelFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Optional;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.GENERATED_AT_TIME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

class StreamingTreeNodeFactoryImplTest {
    private static final String HOST = "http://localhost:8080";
    private static final String COLLECTION = "collectionName";
    private static final String VIEW = "view";
    private static final ViewName VIEW_NAME = new ViewName(COLLECTION, VIEW);
    private static final String FRAGMENTATION_VALUE_1 = "2020-12-28T09:36:09.72Z";
    private TreeNodeRepository treeNodeRepository;
    private TreeMemberRepository treeMemberRepository;
	private StreamingTreeNodeFactory streamingTreeNodeFactory;

    @BeforeEach
    void setUp() {
	    treeNodeRepository = mock(TreeNodeRepository.class);
        treeMemberRepository = mock(TreeMemberRepository.class);
        streamingTreeNodeFactory = new StreamingTreeNodeFactoryImpl(treeNodeRepository, treeMemberRepository);
    }

    @Test
    void when_NoFragmentExists_ThenMissingResourceExceptionIsThrown() {
        LdesFragmentIdentifier id = new LdesFragmentIdentifier(VIEW_NAME,
                List.of(new FragmentPair(GENERATED_AT_TIME, FRAGMENTATION_VALUE_1)));
        Mockito.when(treeNodeRepository.findTreeNodeWithoutMembers(id))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> streamingTreeNodeFactory.getFragmentWithoutMemberData(id))
                .isInstanceOf(MissingResourceException.class)
                .hasMessage("Resource of type: fragment with id: /collectionName/view?generatedAtTime=2020-12-28T09:36:09.72Z could not be found.");
    }

    @Test
    void when_FragmentExists_ThenReturnThatFragment() {
        LdesFragmentIdentifier id = new LdesFragmentIdentifier(VIEW_NAME,
                List.of(new FragmentPair(GENERATED_AT_TIME, FRAGMENTATION_VALUE_1)));
        TreeNode treeNodeWithoutMembers = new TreeNode(HOST + id.asDecodedFragmentId(), true, false, List.of(),
                List.of(), "collectionName", null);
        List<Member> members = List.of(new Member("test", ModelFactory.createDefaultModel()), new Member("test2", ModelFactory.createDefaultModel()));
        TreeNode treeNode = new TreeNode(HOST + id.asDecodedFragmentId(), true, false, List.of(),
                members, "collectionName", null);

        Mockito.when(treeNodeRepository.findTreeNodeWithoutMembers(id))
                .thenReturn(Optional.of(treeNodeWithoutMembers));
        Mockito.when(treeMemberRepository.findAllByTreeNodeUrl(id.asDecodedFragmentId()))
                .thenReturn(members.stream());

        TreeNode returnedTreeNode = streamingTreeNodeFactory.getFragmentWithoutMemberData(id);

        assertThat(returnedTreeNode).isEqualTo(treeNode);
        assertThat(returnedTreeNode.getMembers()).isEmpty();

        List<Member> returnedMembers = streamingTreeNodeFactory.getMembersOfFragment(id).toList();

        assertThat(returnedMembers).containsAll(members);
    }

}