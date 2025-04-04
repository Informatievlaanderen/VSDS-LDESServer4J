package be.vlaanderen.informatievlaanderen.ldes.server.fetching.repository;

import be.vlaanderen.informatievlaanderen.ldes.server.fetching.entities.TreeNode;
import be.vlaanderen.informatievlaanderen.ldes.server.fetching.valueobjects.LdesFragmentIdentifier;

import java.util.Optional;

public interface TreeNodeRepository {
	Optional<TreeNode> findByFragmentIdentifier(LdesFragmentIdentifier fragmentIdentifier);
	Optional<TreeNode> findTreeNodeWithoutMembers(LdesFragmentIdentifier fragmentIdentifier);
}
