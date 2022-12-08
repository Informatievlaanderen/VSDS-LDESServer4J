package be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.node.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.config.LdesConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.PrefixAdder;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.TreeRelation;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.node.entities.TreeNode;
import org.apache.jena.datatypes.TypeMapper;
import org.apache.jena.rdf.model.*;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.*;
import static org.apache.jena.rdf.model.ResourceFactory.*;

@Component
public class TreeNodeConverterImpl implements TreeNodeConverter {

	private final PrefixAdder prefixAdder;
	private final LdesConfig ldesConfig;

	public TreeNodeConverterImpl(PrefixAdder prefixAdder,
			LdesConfig ldesConfig) {
		this.prefixAdder = prefixAdder;
		this.ldesConfig = ldesConfig;
	}

	@Override
	public Model toModel(final TreeNode treeNode) {
		Model model = ModelFactory.createDefaultModel();
		model.add(addTreeNodeStatements(treeNode));
		if (!treeNode.getMembers().isEmpty()) {
			model.add(addEventStreamStatements(treeNode));
			treeNode.getMembers().stream()
					.map(Member::getModel)
					.forEach(model::add);
		}
		return prefixAdder.addPrefixesToModel(model);
	}

	private List<Statement> addTreeNodeStatements(TreeNode treeNode) {
		List<Statement> statements = new ArrayList<>();
		Resource currentFragmentId = createResource(ldesConfig.getHostName() + treeNode.getFragmentId());
		statements.add(createStatement(currentFragmentId, RDF_SYNTAX_TYPE, createResource(TREE_NODE_RESOURCE)));
		statements.addAll(getRelationStatements(treeNode.getRelations(), currentFragmentId));
		return statements;
	}

	private List<Statement> addEventStreamStatements(TreeNode treeNode) {
		List<Statement> statements = new ArrayList<>();
		Resource viewId = createResource(ldesConfig.getHostName() + "/" + ldesConfig.getCollectionName());
		statements.addAll(getEventStreamStatements(viewId));
		statements.addAll(getMemberStatements(treeNode, viewId));
		return statements;
	}

	private List<Statement> getMemberStatements(TreeNode treeNode, Resource viewId) {
		List<Statement> statements = new ArrayList<>();
		treeNode.getMembers()
				.stream().map(Member::getLdesMemberId)
				.forEach(memberId -> statements.add(createStatement(viewId, TREE_MEMBER,
						createResource(memberId))));
		return statements;
	}

	private List<Statement> getEventStreamStatements(Resource viewId) {
		List<Statement> statements = new ArrayList<>();
		statements.add(createStatement(viewId, RDF_SYNTAX_TYPE, createResource(LDES_EVENT_STREAM_URI)));
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

	private List<Statement> getRelationStatements(List<TreeRelation> ldesFragment, Resource currentFragmentId) {
		return ldesFragment.stream()
				.flatMap(treeRelation -> getRelationStatementsOfRelation(currentFragmentId,
						treeRelation).stream())
				.toList();
	}

	private List<Statement> getRelationStatementsOfRelation(Resource currentFragmentId, TreeRelation treeRelation) {
		List<Statement> statements = new ArrayList<>();
		Resource treeRelationNode = createResource();
		statements.add(createStatement(currentFragmentId, TREE_RELATION, treeRelationNode));
		if (hasMeaningfulValue(treeRelation.treeValue()))
			statements.add(createStatement(treeRelationNode, TREE_VALUE, createTypedLiteral(treeRelation.treeValue(),
					TypeMapper.getInstance().getTypeByName(treeRelation.treeValueType()))));
		addStatementIfMeaningful(statements, treeRelationNode, TREE_PATH, treeRelation.treePath());
		addStatementIfMeaningful(statements, treeRelationNode, TREE_NODE,
				ldesConfig.getHostName() + treeRelation.treeNode());
		addStatementIfMeaningful(statements, treeRelationNode, RDF_SYNTAX_TYPE, treeRelation.relation());
		return statements;
	}
}
