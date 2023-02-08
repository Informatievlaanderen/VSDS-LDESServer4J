package be.vlaanderen.informatievlaanderen.ldes.server.domain.fetching;

import org.apache.jena.datatypes.TypeMapper;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.*;
import static org.apache.jena.rdf.model.ResourceFactory.*;

public final class TreeRelationResponse {
	private final String treePath;
	private final String treeNode;
	private final String treeValue;
	private final String treeValueType;
	private final String relation;

	public TreeRelationResponse(String treePath, String treeNode, String treeValue, String treeValueType,
			String relation) {
		this.treePath = treePath;
		this.treeNode = treeNode;
		this.treeValue = treeValue;
		this.treeValueType = treeValueType;
		this.relation = relation;
	}

	public List<Statement> convertToStatements(final String treeNodeId) {
		List<Statement> statements = new ArrayList<>();
		Resource treeRelationNode = createResource();
		statements.add(createStatement(createResource(treeNodeId), TREE_RELATION, treeRelationNode));
		if (hasMeaningfulValue(treeValue))
			statements.add(createStatement(treeRelationNode, TREE_VALUE, createTypedLiteral(treeValue,
					TypeMapper.getInstance().getTypeByName(treeValueType))));
		addStatementIfMeaningful(statements, treeRelationNode, TREE_PATH, treePath);
		addStatementIfMeaningful(statements, treeRelationNode, TREE_NODE,
				treeNode);
		addStatementIfMeaningful(statements, treeRelationNode, RDF_SYNTAX_TYPE, relation);
		return statements;
	}

	private void addStatementIfMeaningful(List<Statement> statements, Resource subject, Property predicate,
			String objectContent) {
		if (hasMeaningfulValue(objectContent))
			statements.add(createStatement(subject, predicate, createResource(objectContent)));
	}

	private boolean hasMeaningfulValue(String objectContent) {
		return objectContent != null && !objectContent.equals("");
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof TreeRelationResponse that))
			return false;
		return Objects.equals(treePath, that.treePath) && Objects.equals(treeNode, that.treeNode)
				&& Objects.equals(treeValue, that.treeValue) && Objects.equals(treeValueType, that.treeValueType)
				&& Objects.equals(relation, that.relation);
	}

	@Override
	public int hashCode() {
		return Objects.hash(treePath, treeNode, treeValue, treeValueType, relation);
	}
}
