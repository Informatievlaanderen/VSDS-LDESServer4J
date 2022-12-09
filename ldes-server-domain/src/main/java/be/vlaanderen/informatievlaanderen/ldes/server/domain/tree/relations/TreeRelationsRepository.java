package be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.relations;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.TreeRelation;

import java.util.List;

public interface TreeRelationsRepository {
	void addTreeRelation(String treeNodeId, TreeRelation relation);

	List<TreeRelation> getRelations(String fragmentId);

	void deleteTreeRelation(String treeNodeId, TreeRelation relation);
}
