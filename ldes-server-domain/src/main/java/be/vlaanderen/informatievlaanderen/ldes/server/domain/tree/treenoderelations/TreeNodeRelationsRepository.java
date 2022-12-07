package be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.treenoderelations;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.TreeRelation;

import java.util.List;

public interface TreeNodeRelationsRepository {
	void addTreeNodeRelation(String treeNodeId, TreeRelation relation);

	List<TreeRelation> getRelations(String fragmentId);
}
