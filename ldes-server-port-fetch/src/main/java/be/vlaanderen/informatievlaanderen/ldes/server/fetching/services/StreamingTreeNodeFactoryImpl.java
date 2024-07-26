package be.vlaanderen.informatievlaanderen.ldes.server.fetching.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.MissingResourceException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.LdesFragmentIdentifier;
import be.vlaanderen.informatievlaanderen.ldes.server.fetching.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.fetching.entities.TreeNode;
import be.vlaanderen.informatievlaanderen.ldes.server.fetching.repository.TreeMemberRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.fetching.repository.TreeNodeRepository;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class StreamingTreeNodeFactoryImpl implements StreamingTreeNodeFactory {

    private final TreeNodeRepository treeNodeRepository;
    private final TreeMemberRepository treeMemberRepository;

    public StreamingTreeNodeFactoryImpl(TreeNodeRepository treeNodeRepository, TreeMemberRepository treeMemberRepository) {
	    this.treeNodeRepository = treeNodeRepository;
	    this.treeMemberRepository = treeMemberRepository;
    }

    @Override
    public TreeNode getFragmentWithoutMemberData(LdesFragmentIdentifier treeNodeId) {
        return treeNodeRepository.findTreeNodeWithoutMembers(treeNodeId)
                .orElseThrow(() -> new MissingResourceException("fragment", treeNodeId.asDecodedFragmentId()));
    }

    @Override
    public Stream<Member> getMembersOfFragment(LdesFragmentIdentifier treeNodeId) {
        return treeMemberRepository.findAllByTreeNodeUrl(treeNodeId.asDecodedFragmentId());
    }
}
