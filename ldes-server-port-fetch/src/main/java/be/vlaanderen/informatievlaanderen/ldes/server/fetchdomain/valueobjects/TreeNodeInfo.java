package be.vlaanderen.informatievlaanderen.ldes.server.fetchdomain.valueobjects;

import org.apache.jena.rdf.model.Statement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static be.vlaanderen.informatievlaanderen.ldes.server.fetchdomain.constants.RdfConstants.RDF_SYNTAX_TYPE;
import static be.vlaanderen.informatievlaanderen.ldes.server.fetchdomain.constants.RdfConstants.TREE_NODE_RESOURCE;
import static org.apache.jena.rdf.model.ResourceFactory.createResource;
import static org.apache.jena.rdf.model.ResourceFactory.createStatement;

public class TreeNodeInfo {

	private final String treeNodeId;
	private final List<TreeRelation> treeRelations;

	public TreeNodeInfo(String treeNodeId, List<TreeRelation> treeRelations) {
		this.treeNodeId = treeNodeId;
		this.treeRelations = treeRelations;
	}

	public List<Statement> convertToStatements() {
		List<Statement> statements = new ArrayList<>();
		statements
				.add(createStatement(createResource(treeNodeId), RDF_SYNTAX_TYPE, createResource(TREE_NODE_RESOURCE)));
		treeRelations.forEach(
				treeRelationResponse -> statements.addAll(treeRelationResponse.convertToStatements(treeNodeId)));
		return Collections.unmodifiableList(statements);
	}

	public List<String> getTreeNodeIdsInRelations() {
		return treeRelations
				.stream()
				.map(TreeRelation::getTreeNodeId)
				.toList();
	}
}
