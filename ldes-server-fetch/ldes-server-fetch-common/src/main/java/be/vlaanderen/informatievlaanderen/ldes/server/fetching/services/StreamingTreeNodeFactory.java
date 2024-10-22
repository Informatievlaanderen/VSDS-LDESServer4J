package be.vlaanderen.informatievlaanderen.ldes.server.fetching.services;

import be.vlaanderen.informatievlaanderen.ldes.server.fetching.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.fetching.entities.TreeNode;
import be.vlaanderen.informatievlaanderen.ldes.server.fetching.valueobjects.LdesFragmentIdentifier;

import java.util.stream.Stream;

public interface StreamingTreeNodeFactory {
    TreeNode getFragmentWithoutMemberData(LdesFragmentIdentifier treeNodeId);

    Stream<Member> getMembersOfFragment(LdesFragmentIdentifier treeNodeId);
}
