package be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.node.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.config.LdesConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.PrefixAdder;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.node.entities.TreeNode;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.relations.services.RelationStatementConverter;
import org.apache.jena.rdf.model.*;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.*;
import static org.apache.jena.rdf.model.ResourceFactory.createResource;
import static org.apache.jena.rdf.model.ResourceFactory.createStatement;

@Component
public class TreeNodeConverterImpl implements TreeNodeConverter {

	private final PrefixAdder prefixAdder;
	private final LdesConfig ldesConfig;
	private final RelationStatementConverter relationStatementConverter;

	public TreeNodeConverterImpl(PrefixAdder prefixAdder,
			LdesConfig ldesConfig, RelationStatementConverter relationStatementConverter) {
		this.prefixAdder = prefixAdder;
		this.ldesConfig = ldesConfig;
		this.relationStatementConverter = relationStatementConverter;
	}

	@Override
	public Model toModel(final TreeNode treeNode) {
		Model model = ModelFactory.createDefaultModel();
		model.add(addTreeNodeStatements(treeNode));

		if (!treeNode.getMembers().isEmpty()) {
			model.add(addEventStreamStatements(treeNode));
			treeNode.getMembers().stream()
					.map(Member::getModel).forEach(model::add);
		}

		return prefixAdder.addPrefixesToModel(model);
	}

	private List<Statement> addTreeNodeStatements(TreeNode treeNode) {
		List<Statement> statements = new ArrayList<>();
		Resource currentFragmentId = createResource(
				ldesConfig.getHostName() + "/" + ldesConfig.getCollectionName() + treeNode.getFragmentId());

		statements.add(createStatement(currentFragmentId, RDF_SYNTAX_TYPE, createResource(TREE_NODE_RESOURCE)));
		statements.addAll(relationStatementConverter.getRelationStatements(treeNode.getRelations(), currentFragmentId));

		addLdesCollectionStatements(statements, treeNode.isView(), currentFragmentId);

		return statements;
	}

	private void addLdesCollectionStatements(List<Statement> statements, boolean isView, Resource currentFragmentId) {
		Resource collection = createResource(ldesConfig.getHostName() + "/" + ldesConfig.getCollectionName());

		if (isView) {
			statements.add(createStatement(collection, RDF_SYNTAX_TYPE, createResource(LDES_EVENT_STREAM_URI)));
			addStatementIfMeaningful(statements, collection, TREE_SHAPE, ldesConfig.validation().getShape());
			addStatementIfMeaningful(statements, collection, LDES_VERSION_OF, ldesConfig.getVersionOfPath());
			addStatementIfMeaningful(statements, collection, LDES_TIMESTAMP_PATH, ldesConfig.getTimestampPath());
			statements.add(createStatement(collection, TREE_VIEW, currentFragmentId));
		} else {
			statements.add(createStatement(currentFragmentId, IS_PART_OF_PROPERTY, collection));
		}
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

}
