package be.vlaanderen.informatievlaanderen.ldes.server.domain.fetching;

import org.apache.jena.rdf.model.Statement;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.RDF_SYNTAX_TYPE;
import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.TREE_NODE_RESOURCE;
import static org.apache.jena.rdf.model.ResourceFactory.createResource;
import static org.apache.jena.rdf.model.ResourceFactory.createStatement;

public final class TreeNodeInfoResponse {
	private final String treeNodeId;
	private final List<TreeRelationResponse> treeRelationResponses;

	public TreeNodeInfoResponse(String treeNodeId, List<TreeRelationResponse> treeRelationResponses) {
		this.treeNodeId = treeNodeId;
		this.treeRelationResponses = treeRelationResponses;
	}

	public List<Statement> convertToStatements() {
		List<Statement> statements = new ArrayList<>();
		statements
				.add(createStatement(createResource(treeNodeId), RDF_SYNTAX_TYPE, createResource(TREE_NODE_RESOURCE)));
		treeRelationResponses.forEach(
				treeRelationResponse -> statements.addAll(treeRelationResponse.convertToStatements(treeNodeId)));
		return statements;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof TreeNodeInfoResponse that))
			return false;
		return Objects.equals(treeNodeId, that.treeNodeId)
				&& Objects.equals(treeRelationResponses, that.treeRelationResponses);
	}

	@Override
	public int hashCode() {
		return Objects.hash(treeNodeId, treeRelationResponses);
	}
}
